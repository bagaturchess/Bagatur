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
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.state.PiecesList;

public class CheckingCount extends Fields {
	
	
	/**
	 * Any check.
	 */
	public static final int getChecksCount(Board board, int colour, int opponentColour,
			long kingBitboard, int kingFieldID, long free) {
		
		return getFieldAttacksCount(null, board, opponentColour, colour, kingBitboard, kingFieldID, free);		
	}
	
	public static final int getChecksCount(Checker buff, Board board, int colour, int opponentColour,
			long kingBitboard, int kingFieldID, long free) {
		
		return getFieldAttacksCount(buff, board, opponentColour, colour, kingBitboard, kingFieldID, free);		
	}
	
	/**
	 * Any check.
	 */
	public static final int getFieldAttacksCount(Checker buff, Board board, int attackingColour, int opponentColour,
			long fieldBitboard, int fieldID, long free) {
		
		int result = 0;
		
		result += getUncoveredChecksCount(buff, board, opponentColour, attackingColour, fieldBitboard, fieldID);

		IPiecesLists stateManager = board.getPiecesLists();
		
		//Officer and Queens checks
		long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
		long opponentOfficers = board.allByColourAndType[attackingColour][Figures.TYPE_OFFICER];
		if ((officerMoves & opponentOfficers) != NUMBER_0) {
			PiecesList oppOfficers = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_OFFICER));
			result += getOfficersChecksCount(buff, board, attackingColour, fieldID, free, officerMoves, oppOfficers, Figures.TYPE_OFFICER);
		}
		
		long opponentQueens = board.allByColourAndType[attackingColour][Figures.TYPE_QUEEN];
		PiecesList oppQueens = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_QUEEN));
		if ((officerMoves & opponentQueens) != NUMBER_0) {
			result += getOfficersChecksCount(buff, board, attackingColour, fieldID, free, officerMoves, oppQueens, Figures.TYPE_QUEEN);
		}
		
		//Castle and Queens checks
		long castleMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID];
		long opponentCastles = board.allByColourAndType[attackingColour][Figures.TYPE_CASTLE];
		
		if ((castleMoves & opponentCastles) != NUMBER_0) {
			PiecesList oppCastles = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_CASTLE));
			result += getCastlesChecksCount(buff, board, attackingColour ,fieldID, free, castleMoves, oppCastles, Figures.TYPE_CASTLE);
		}
		
		if ((castleMoves & opponentQueens) != NUMBER_0) {
			result += getCastlesChecksCount(buff, board, attackingColour, fieldID, free, castleMoves, oppQueens, Figures.TYPE_QUEEN);
		}
		
		return result;
	}

	/**
	 * Check from knight or pawns without double checks.
	 */
	public static final int getUncoveredChecksCount(Checker buff, Board board, int colour, int attackingColour, long kingBitboard, int kingFieldID) {
		
		int result = 0;
		
		//Check knights
		long opponentKnights = board.allByColourAndType[attackingColour][Figures.TYPE_KNIGHT];
		if (opponentKnights != NUMBER_0)  {
			long potentialKnightsAttacks = KnightPlies.ALL_KNIGHT_MOVES[kingFieldID];
			if ((opponentKnights & potentialKnightsAttacks) != NUMBER_0) {
				result++;
				if (buff != null) {
					PiecesList opponentKnightsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_KNIGHT));
					int size = opponentKnightsIDs.getDataSize();
					int[] ids = opponentKnightsIDs.getData();
					for (int i=0; i<size; i++) {
						int fieldID = ids[i];
						long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
						if ((figureBoard & potentialKnightsAttacks) != NUMBER_0) {
							buff.slider = false;
							buff.figureType = Figures.TYPE_KNIGHT;
							//buff.figureID = figureID;
							buff.fieldID = fieldID;
							buff.fieldBitboard = figureBoard;
							buff.sliderAttackRayBitboard = 0L;
							break;
						} else if (i == size - 1) {
							if (Properties.DEBUG_MODE) throw new IllegalStateException();
						}
					}
				}
			}
		}
		
		//Check opponent king
		long opponentKing = board.allByColourAndType[attackingColour][Figures.TYPE_KING];
		if (opponentKing != NUMBER_0)  {
			long potentialKingAttacks = KingPlies.ALL_KING_MOVES[kingFieldID];
			if ((opponentKing & potentialKingAttacks) != NUMBER_0) {
				if (Properties.DEBUG_MODE) throw new IllegalStateException();
				//result++;
			}
		}
		
		//Check pawns attacks
		long opponentPawns = board.allByColourAndType[attackingColour][Figures.TYPE_PAWN];
		if (opponentPawns != NUMBER_0)  {
			switch (colour) {
				case Figures.COLOUR_WHITE:
					long nonactivePawns = opponentPawns & Fields.LETTER_A;
					long activePawns = opponentPawns & ~nonactivePawns;
					long attacks = activePawns << 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						result++;
						if (buff != null) {
							PiecesList opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_PAWN));
							int size = opponentPawnsIDs.getDataSize();
							int[] ids = opponentPawnsIDs.getData();
							for (int i=0; i<size; i++) {
								int fieldID = ids[i];
								long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
								if (((figureBoard << 9) & kingBitboard) != NUMBER_0) {
									buff.slider = false;
									buff.figureType = Figures.TYPE_PAWN;
									//buff.figureID = figureID;
									buff.fieldID = fieldID;
									buff.fieldBitboard = figureBoard;
									buff.sliderAttackRayBitboard = 0L;
									break;
								} else if (i == size - 1) {
									if (Properties.DEBUG_MODE) throw new IllegalStateException();
								}
							}
						}
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns << 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						result++;
						if (buff != null) {
							PiecesList opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_PAWN));
							int size = opponentPawnsIDs.getDataSize();
							int[] ids = opponentPawnsIDs.getData();
							for (int i=0; i<size; i++) {
								int fieldID = ids[i];
								long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
								if (((figureBoard << 7) & kingBitboard) != NUMBER_0) {
									buff.slider = false;
									buff.figureType = Figures.TYPE_PAWN;
									//buff.figureID = figureID;
									buff.fieldID = fieldID;
									buff.fieldBitboard = figureBoard;
									buff.sliderAttackRayBitboard = 0L;
									break;
								} else if (i == size - 1) {
									if (Properties.DEBUG_MODE) throw new IllegalStateException();
								}
							}
						}
					}
					break;
				case Figures.COLOUR_BLACK:
					nonactivePawns = opponentPawns & Fields.LETTER_A;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						result++;
						if (buff != null) {
							PiecesList opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_PAWN));
							int size = opponentPawnsIDs.getDataSize();
							int[] ids = opponentPawnsIDs.getData();
							for (int i=0; i<size; i++) {
								int fieldID = ids[i];
								long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
								if (((figureBoard >> 7) & kingBitboard) != NUMBER_0) {
									buff.slider = false;
									buff.figureType = Figures.TYPE_PAWN;
									//buff.figureID = figureID;
									buff.fieldID = fieldID;
									buff.fieldBitboard = figureBoard;
									buff.sliderAttackRayBitboard = 0L;
									break;
								} else if (i == size - 1) {
									if (Properties.DEBUG_MODE) throw new IllegalStateException();
								}
							}
						}
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						result++;
						if (buff != null) {
							PiecesList opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_PAWN));
							int size = opponentPawnsIDs.getDataSize();
							int[] ids = opponentPawnsIDs.getData();
							for (int i=0; i<size; i++) {
								int fieldID = ids[i];
								long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
								if (((figureBoard >> 9) & kingBitboard) != NUMBER_0) {
									buff.slider = false;
									buff.figureType = Figures.TYPE_PAWN;
									//buff.figureID = figureID;
									buff.fieldID = fieldID;
									buff.fieldBitboard = figureBoard;
									buff.sliderAttackRayBitboard = 0L;
									break;
								} else if (i == size - 1) {
									if (Properties.DEBUG_MODE) throw new IllegalStateException();
								}
							}
						}
					}
					break;
			}
		}
		
		return result;
	}
	
	private static int getOfficersChecksCount(Checker buff, Board board, int attackColour,
			int kingFieldID, long free, long officerMoves, PiecesList oppOfficers, int checkingFigureType) {
		int result = 0;
		int count = oppOfficers.getDataSize();
		int[] data = oppOfficers.getData();
		for (int i=0; i<count; i++) {
			int oppOfficerID = data[i];
			long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
			if ((officerMoves & oppOfficerBitboard) != NUMBER_0) {
				/**
				 * Determine is oppOfficerBitboard makes this potential check.
				 */
				result += getOfficerDirsChecksCount(buff, board, attackColour, kingFieldID, free, oppOfficerBitboard, checkingFigureType);
			}
		}
		return result;
	}

	private static int getCastlesChecksCount(Checker buff, Board board, int attackColour,
			int kingFieldID, long free, long castleMoves, PiecesList oppCastles, int checkingFigureType) {
		int result = 0;
		
		int count = oppCastles.getDataSize();
		int[] data = oppCastles.getData();
		for (int i=0; i<count; i++) {
			int oppCastleID = data[i];
			long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
			if ((castleMoves & oppCastleBitboard) != NUMBER_0) {
				/**
				 * Determine is oppCastleBitboard makes this potential check.
				 */
				result += getCastleDirsChecksCount(buff, board, attackColour, kingFieldID, free, oppCastleBitboard, checkingFigureType);
			}
		}
		return result;
	}
	
	private static int getOfficerDirsChecksCount(Checker buff, Board board, int attackColour,
			int kingFieldID, long free, long oppOfficerBitboard, int checkingFigureType) {
		
		int result = 0;
		
		long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[kingFieldID];
		if ((dir0 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][0];
			if (checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir0Moves, checkingFigureType))
				result++;
		}

		long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[kingFieldID];
		if ((dir1 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][1];
			if (checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir1Moves, checkingFigureType))
				result++;
		}

		long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[kingFieldID];
		if ((dir2 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][2];
			if (checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir2Moves, checkingFigureType))
				result++;
		}

		long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[kingFieldID];
		if ((dir3 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][3];
			if (checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir3Moves, checkingFigureType))
				result++;
		}
		
		return result;
	}

	private static int getCastleDirsChecksCount(Checker buff, Board board, int attackColour,
			int kingFieldID, long free, long oppCastleBitboard, int checkingFigureType) {
		
		int result = 0;
		
		long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[kingFieldID];
		if ((dir0 & oppCastleBitboard) != NUMBER_0) {
			long[] dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][0];
			if (checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir0Moves, checkingFigureType))
				result++;
		}

		long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[kingFieldID];
		if ((dir1 & oppCastleBitboard) != NUMBER_0) {
			long[] dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][1];
			if (checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir1Moves, checkingFigureType))
				result++;
		}

		long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[kingFieldID];
		if ((dir2 & oppCastleBitboard) != NUMBER_0) {
			long[] dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][2];
			if (checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir2Moves, checkingFigureType))
				result++;
		}

		long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[kingFieldID];
		if ((dir3 & oppCastleBitboard) != NUMBER_0) {
			long[] dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][3];
			if (checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir3Moves, checkingFigureType))
				result++;
		}
		
		return result;
	}
	
	private static boolean checkSlidingDir(Checker buff, Board board, int attackingColour,
			long free, long oppAttackFigure, long[] dirMoves, int checkingFigureType) {
		/**
		 * The oppOfficerBitboard interacts with dir.
		 * Let's traverse that direction to determine is the check real.
		 */
		long ray = 0L;
		for (int j=0; j<dirMoves.length; j++) {
			long officerMove = dirMoves[j];
			if ((officerMove & oppAttackFigure) != NUMBER_0) {
				if (buff != null) {
					PiecesList opponentKnightsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, checkingFigureType));
					int size = opponentKnightsIDs.getDataSize();
					int[] ids = opponentKnightsIDs.getData();
					for (int i=0; i<size; i++) {
						int fieldID = ids[i];
						long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
						if ((figureBoard & officerMove) != NUMBER_0) {
							buff.slider = true;
							buff.figureType = checkingFigureType;

							buff.fieldID = fieldID;
							buff.fieldBitboard = figureBoard;
							buff.sliderAttackRayBitboard = ray;
							break;
						} else if (i == size - 1) {
							throw new IllegalStateException();
						}
					}
				}
				return true;
			}
			if ((officerMove & free) == NUMBER_0) {
				break; //This is figure different from officerMove, there is no interest here.
			}
			ray |= officerMove;
		}
		
		return false;
	}
}
