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

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.checking.BlackPawnsChecks;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;

/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class BlackPawnMovesGen extends BlackPawnsChecks {
	
	private static long[][] middleField = new long[Fields.PRIME_67][Fields.PRIME_67];
	static {
		middleField[get67IDByBitboard(A7)][get67IDByBitboard(A5)] = A6;
		middleField[get67IDByBitboard(B7)][get67IDByBitboard(B5)] = B6;
		middleField[get67IDByBitboard(C7)][get67IDByBitboard(C5)] = C6;
		middleField[get67IDByBitboard(D7)][get67IDByBitboard(D5)] = D6;
		middleField[get67IDByBitboard(E7)][get67IDByBitboard(E5)] = E6;
		middleField[get67IDByBitboard(F7)][get67IDByBitboard(F5)] = F6;
		middleField[get67IDByBitboard(G7)][get67IDByBitboard(G5)] = G6;
		middleField[get67IDByBitboard(H7)][get67IDByBitboard(H5)] = H6;
	}
	
	static final int figureType = Figures.TYPE_PAWN;
	
	static final int[][] attacksValidDirs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS;
	static final int[][] nonattacksValidDirs = ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS;
	
	static final int[][][] attacksFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS;
	static final int[][][] nonattacksFieldIDs = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS;

	static final long[][][] attacksBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS;
	static final long[][][] nonattacksBitboards = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS;
	
	public static final int genAllMoves(final IBitBoard board,
			final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
			//final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			boolean hasEnpassant,
			final long enpassantPawnBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int [] validDirIDs = attacksValidDirs[fromFieldID];
		int[][] dirs_ids = attacksFieldIDs[fromFieldID];
		long[][] dirs = attacksBitboards[fromFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			final int toFieldID = dirs_ids[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				if (hasEnpassant //&& enpassantColour == figureColour
					&& !Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID])
					//&& (toBitboard & allOpponentBitboard) == NUMBER_0
					&& (excludedToFieldsIDs & enpassantPawnBitboard) == NUMBER_0) {
				} else 
					continue;
			}
			
			/**
			 * Captures
			 */
			//if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
			if (Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID])) {
				
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					/**
					 * Captures-Promotions
					 */
					if (list != null) {
							int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
							list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_QUEEN));
							list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_ROOK));
							list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_BISHOP));
							list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_KNIGHT));
					}
					
					count += 4;
					
					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return count;
					}

				} else {
					/**
					 * Captures-NonPormotions
					 */
					
					if (list != null) {
						int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];						
						list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
				}
				
			} else if (hasEnpassant) { // && enpassantColour == figureColour) {
				
				/**
				 * Enpasant
				 */
				int enpassCount = genEnpassantMove(board, excludedToFieldsIDs, figureID, 
						fromFieldID, 0,//FIXME: allOpponentBitboard,
						figuresIDsPerFieldsIDs,
						hasEnpassant, enpassantPawnBitboard,
						list, maxCount);
				if (enpassCount > 0) {
					hasEnpassant = false;
					count += enpassCount;
					
					if (count >= maxCount) {
						return count;
					}
				}
			}
		}
		
		validDirIDs = nonattacksValidDirs[fromFieldID];
		dirs_ids = nonattacksFieldIDs[fromFieldID];
		dirs = nonattacksBitboards[fromFieldID];
		
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
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
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					/**
					 * NonCaptures-Promotions
					 */
					if (list != null) {
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_QUEEN));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_ROOK));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_BISHOP));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_KNIGHT));
					}
					count += 4;

					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return maxCount;
					}
					
				} else {
					/**
					 * NonCaptures-NonPromotions
					 */
					
					if (list != null) {
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
				}
				
			} else {
				break;
			}
		}
		
		return count;
	}
	
	public static final int genAllNonSpecialMoves(
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
		
		int [] validDirIDs = attacksValidDirs[fromFieldID];
		int[][] dirs_ids = attacksFieldIDs[fromFieldID];
		long[][] dirs = attacksBitboards[fromFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				continue;
			}
			
			/**
			 * Captures
			 */
			if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					//Do nothink
				} else {
					/**
					 * Captures-NonPormotions
					 */
					
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
		}
		
		validDirIDs = nonattacksValidDirs[fromFieldID];
		dirs_ids = nonattacksFieldIDs[fromFieldID];
		dirs = nonattacksBitboards[fromFieldID];
		
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
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
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					//Do nothing
				} else {
					/**
					 * NonCaptures-NonPromotions
					 */
					
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][0];
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
				}
				
			} else {
				break;
			}
		}
		
		return count;
	}
	
	public static final int genEnpassantMove(final IBitBoard board,
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final boolean hasEnpassant,
			final long enpassantPawnBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int [] validDirIDs = attacksValidDirs[fromFieldID];
		int[][] dirs_ids = attacksFieldIDs[fromFieldID];
		long[][] dirs = attacksBitboards[fromFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			final int toFieldID = dirs_ids[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				if (hasEnpassant// && enpassantColour == figureColour
					//&& (toBitboard & allOpponentBitboard) == NUMBER_0
					&& !Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID])
					&& (excludedToFieldsIDs & enpassantPawnBitboard) == NUMBER_0) {
				} else 
					continue;
			}
			
			if (hasEnpassant) {// && enpassantColour == figureColour) {
				
				/**
				 * Enpasant
				 */
				int figureColour = Figures.getFigureColour(figureID);
				long opponentPawnBitboard = Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[figureColour][fromFieldID][dirID];
				
				if ((enpassantPawnBitboard & opponentPawnBitboard) != NUMBER_0) {
					
					int capturedFieldID = Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[figureColour][fromFieldID][dirID];
					int capturedPawnID = figuresIDsPerFieldsIDs[capturedFieldID];
					int enpassMove = MoveInt.createEnpassant(figureID, fromFieldID, toFieldID, dirID, capturedPawnID);
					
					/**
					 * Verify legality 
					 */
					
					((Board)board).makeMoveForward(enpassMove, false);
					boolean legal = !board.isInCheck(figureColour);
					((Board)board).makeMoveBackward(enpassMove, false);
					
					if (legal) {
						count++;
						
						if (list != null) {
							list.reserved_add(enpassMove);
						}
						
						if (count >= maxCount) {
							return count;
						}
						
						break;
					}
				} 
			}
		}
		
		return count;
	}
	
	public static final int genCapturePromotionEnpassantMoves(final IBitBoard board,
			final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final int figureID, 
			final int fromFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			boolean hasEnpassant,
			final long enpassantPawnBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int [] validDirIDs = attacksValidDirs[fromFieldID];
		int[][] dirs_ids = attacksFieldIDs[fromFieldID];
		long[][] dirs = attacksBitboards[fromFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				if (hasEnpassant// && enpassantColour == figureColour
					&& (toBitboard & allOpponentBitboard) == NUMBER_0
					&& (excludedToFieldsIDs & enpassantPawnBitboard) == NUMBER_0) {
				} else 
					continue;
			}
			
			/**
			 * Captures
			 */
			if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					/**
					 * Captures-Promotions
					 */
					if (list != null) {
						int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_QUEEN));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_ROOK));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_BISHOP));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_KNIGHT));
					}
					count += 4;
					
					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return maxCount;
					}
					
				} else {
					/**
					 * Captures-NonPormotions
					 */
					
					if (list != null) {
						int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
				}
				
			} else if (hasEnpassant) {// && enpassantColour == figureColour) {
				
				/**
				 * Enpasant
				 */
				int enpassCount = genEnpassantMove(board, excludedToFieldsIDs, figureID, 
						fromFieldID, allOpponentBitboard,
						figuresIDsPerFieldsIDs,
						hasEnpassant, enpassantPawnBitboard,
						list, maxCount);
				if (enpassCount > 0) {
					hasEnpassant = false;
					count += enpassCount;
					
					if (count >= maxCount) {
						return count;
					}
				}
			}
		}
		
		validDirIDs = nonattacksValidDirs[fromFieldID];
		dirs_ids = nonattacksFieldIDs[fromFieldID];
		dirs = nonattacksBitboards[fromFieldID];
		
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
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
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if ((toBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
					/**
					 * NonCaptures-Promotions
					 */
					if (list != null) {
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_QUEEN));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_ROOK));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_BISHOP));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_KNIGHT));
					}
					count += 4;
					
					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return maxCount;
					}
				}
			} else {
				break;
			}
		}
		
		return count;
	}
	
	public static final int genPromotionMoves(final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final long fromBitboard,
			final int fromFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		if ((fromBitboard & DIGIT_2) != NUMBER_0) {
			int [] validDirIDs = attacksValidDirs[fromFieldID];
			int[][] dirs_ids = attacksFieldIDs[fromFieldID];
			long[][] dirs = attacksBitboards[fromFieldID];
			
			int size = validDirIDs.length;
			
			for (int i=0; i<size; i++) {
				
				int dirID = validDirIDs[i];
				long toBitboard = dirs[dirID][0];
			
				if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
					continue;
				}
				
				/**
				 * Captures
				 */
				if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
					
					final int toFieldID = dirs_ids[dirID][0];
					
					if ((toBitboard & Fields.BLACK_PROMOTIONS) == NUMBER_0) {
						throw new IllegalStateException();
					}
					/**
					 * Captures-Promotions
					 */
					if (list != null) {
						int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_QUEEN));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_ROOK));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_BISHOP));
						list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, Constants.PID_B_KNIGHT));
					}
					count += 4;
					
					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return maxCount;
					}
				}
			}
			
			validDirIDs = nonattacksValidDirs[fromFieldID];
			dirs_ids = nonattacksFieldIDs[fromFieldID];
			dirs = nonattacksBitboards[fromFieldID];
			
			size = validDirIDs.length;
			
			for (int i=0; i<size; i++) {
				
				int dirID = validDirIDs[i];
				long toBitboard = dirs[dirID][0];
	
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
					if ((toBitboard & Fields.BLACK_PROMOTIONS) == NUMBER_0) {
						throw new IllegalStateException();
					}
					/**
					 * NonCaptures-Promotions
					 */
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][0];
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_QUEEN));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_ROOK));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_BISHOP));
						list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, Constants.PID_B_KNIGHT));
					}
					count += 4;
					
					/**
					 * maxCount instead of count because increment is with 4 instead of 1.
					 */
					if (count >= maxCount) {
						return maxCount;
					}
				}
			}
		}
		
		return count;
	}
	
	/**
	 * ! Without promotions 
	 */
	public static final int genNonCaptureMoves(
			final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int[] validDirIDs = nonattacksValidDirs[fromFieldID];
		int[][] dirs_ids = nonattacksFieldIDs[fromFieldID];
		long[][] dirs = nonattacksBitboards[fromFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];

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
				if ((toBitboard & Fields.BLACK_PROMOTIONS) == NUMBER_0) {
					/**
					 * NonCaptures-NonPromotions
					 */
					
					if (list != null) {						
						final int toFieldID = dirs_ids[dirID][0];
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
				}
				
			} else {
				break;
			}
		}
		
		return count;
	}
	
	public static final boolean isPossible(final int move,
			final int[] figuresIDsPerFieldsIDs, final long free,
			final boolean hasEnpassant,
			final long enpassantPawnBitboard) {
		
		/*if (move[3] != figureType) {
			throw new IllegalStateException();
		}
		if (move[2] != Figures.COLOUR_BLACK) {
			throw new IllegalStateException();
		}*/
		
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
			if (MoveInt.isEnpassant(move)) {
				if (hasEnpassant) { //HACK && enpassantColour == move[2]
					long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getEnpassantCapturedFieldID(move)];
					if (enpassantPawnBitboard != opponentPawnBitboard) {
						return false;
					}
				} else
					return false;
			} else {
				int capturedFigureID = MoveInt.getCapturedFigurePID(move);
				if (figuresIDsPerFieldsIDs[toFieldID] != capturedFigureID) {
					return false;
				}
			}
		} else {
			if (figuresIDsPerFieldsIDs[toFieldID] != Constants.PID_NONE) {
				return false;
			}
			
			if (middleField[fromFieldID][toFieldID] != NUMBER_0
					&& (middleField[fromFieldID][toFieldID] & free) == NUMBER_0) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * ! Without promotions and enpassant
	 */
	public static final int genCheckMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final int opponentKingFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int[] fields =  CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
		//int[] dirs = CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][opponentKingFieldID];
		//int[] seqs = CHECK_NONATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][opponentKingFieldID];
		long[] fieldBoards =  CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
		
		if (fields != null) {
			int size = fields.length;
		
			for (int i=0; i<size; i++) {
				long middleFieldBitboard = fieldBoards[i];

				if ((excludedToFieldsIDs & middleFieldBitboard) != NUMBER_0) {
					continue;
				}
				
				int toFieldID = fields[i];
				if (middleField[fromFieldID][toFieldID] != NUMBER_0
						&& (middleField[fromFieldID][toFieldID] & freeBitboard) == NUMBER_0) {
					continue;
				}
				
				if ((middleFieldBitboard & freeBitboard) != NUMBER_0) {
					
					if ((middleFieldBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
						throw new IllegalStateException();
					}
					
					/**
					 * NonCaptures-NonPromotions
					 */
					
					if (list != null) {
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
					
				} else {
					break;
				}
			}
		}
		
		
		fields =  CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
		//dirs = CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][opponentKingFieldID];
		//seqs = CHECK_ATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][opponentKingFieldID];
		fieldBoards =  CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
		
		if (fields != null) {
			int size = fields.length;
		
			for (int i=0; i<size; i++) {
				long middleFieldBitboard = fieldBoards[i];
				
				if ((excludedToFieldsIDs & middleFieldBitboard) != NUMBER_0) {
					continue;
				}
				
				/**
				 * Captures
				 */
				if ((middleFieldBitboard & allOpponentBitboard) != NUMBER_0) {
					if ((middleFieldBitboard & Fields.BLACK_PROMOTIONS) != NUMBER_0) {
						/**
						 * Captures-Promotions
						 */
						throw new IllegalStateException();
					} else {
						/**
						 * Captures-NonPormotions
						 */

						if (list != null) {
							int toFieldID = fields[i];
							int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
							list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
						}
						count++;
						
						if (count >= maxCount) {
							return count;
						}
					}
				}
			}
		}
		
		return count;
	}
}
