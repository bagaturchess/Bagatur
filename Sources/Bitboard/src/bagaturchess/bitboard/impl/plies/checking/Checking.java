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
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.state.PiecesList;


public class Checking extends Fields {
	
	/**
	 * Any check.
	 */
	public static final boolean isFieldAttacked(Board board, int attackingColour, int opponentColour,
			long fieldBitboard, int fieldID, long free, boolean kingAttacksPossible) {
		
		if (isInUncoveredCheck(board, opponentColour, attackingColour, fieldBitboard, fieldID, kingAttacksPossible)) {
			return true;
		}

		IPiecesLists stateManager = board.getPiecesLists();
		
		//Officer and Queens checks
		
		long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
		
		long opponentQueens = board.allByColourAndType[attackingColour][Figures.TYPE_QUEEN];
		PiecesList oppQueens = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_QUEEN));
		if ((officerMoves & opponentQueens) != NUMBER_0) {
			if (checkOfficers(board, fieldID, free, officerMoves, oppQueens)) {
				return true;
			}
		}
		
		long opponentOfficers = board.allByColourAndType[attackingColour][Figures.TYPE_OFFICER];
		if ((officerMoves & opponentOfficers) != NUMBER_0) {
			PiecesList oppOfficers = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_OFFICER));
			if (checkOfficers(board, fieldID, free, officerMoves, oppOfficers)) {
				return true;
			}
		}
		
		//Castle and Queens checks
		long castleMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID];
		
		if ((castleMoves & opponentQueens) != NUMBER_0) {
			if (checkCastles(board, fieldID, free, castleMoves, oppQueens)) {
				return true;
			}
		}
		
		long opponentCastles = board.allByColourAndType[attackingColour][Figures.TYPE_CASTLE];
		if ((castleMoves & opponentCastles) != NUMBER_0) {
			PiecesList oppCastles = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, Figures.TYPE_CASTLE));
			if (checkCastles(board, fieldID, free, castleMoves, oppCastles)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Any check.
	 */
	public static final boolean isInCheck(Board board, int colour, int opponentColour,
			long kingBitboard, int kingFieldID, long free, boolean kingAttackPossible) {
		
		return isFieldAttacked(board, opponentColour, colour, kingBitboard, kingFieldID, free, kingAttackPossible);		
	}

	/**
	 * Check from knight or pawns without double checks.
	 */
	public static final boolean isInUncoveredCheck(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID, boolean kingAttacksPossible) {
		
		//Check knights

		long opponentKnights = board.allByColourAndType[opponentColour][Figures.TYPE_KNIGHT];
		if (opponentKnights != NUMBER_0)  {
			long potentialKnightsAttacks = KnightPlies.ALL_KNIGHT_MOVES[kingFieldID];
			if ((opponentKnights & potentialKnightsAttacks) != NUMBER_0) {
				return true;
			}
		}
		
		//Check opponent king
		long opponentKing = board.allByColourAndType[opponentColour][Figures.TYPE_KING];
		if (opponentKing != NUMBER_0)  {
			long potentialKingAttacks = KingPlies.ALL_KING_MOVES[kingFieldID];
			if ((opponentKing & potentialKingAttacks) != NUMBER_0) {
				if (!kingAttacksPossible) {
					throw new IllegalStateException("King attack");
				}
				return true;
			}
		}
		
		//Check pawns attacks
		long opponentPawns = board.allByColourAndType[opponentColour][Figures.TYPE_PAWN];
		if (opponentPawns != NUMBER_0)  {
			switch (colour) {
				case Figures.COLOUR_WHITE:
					long nonactivePawns = opponentPawns & Fields.LETTER_A;
					long activePawns = opponentPawns & ~nonactivePawns;
					long attacks = activePawns << 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns << 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					break;
				case Figures.COLOUR_BLACK:
					nonactivePawns = opponentPawns & Fields.LETTER_A;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					break;
			}
		}
		
		return false;
	}
	
	public static final boolean isAttackedFromPawns(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID) {
		
		//Check pawns attacks
		long opponentPawns = board.allByColourAndType[opponentColour][Figures.TYPE_PAWN];
		if (opponentPawns != NUMBER_0)  {
			switch (colour) {
				case Figures.COLOUR_WHITE:
					long nonactivePawns = opponentPawns & Fields.LETTER_A;
					long activePawns = opponentPawns & ~nonactivePawns;
					long attacks = activePawns << 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns << 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					break;
				case Figures.COLOUR_BLACK:
					nonactivePawns = opponentPawns & Fields.LETTER_A;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 7;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					nonactivePawns = opponentPawns & Fields.LETTER_H;
					activePawns = opponentPawns & ~nonactivePawns;
					attacks = activePawns >> 9;
					if ((kingBitboard & attacks) != NUMBER_0) {
						return true;
					}
					break;
			}
		}
		
		return false;
	}
	
	private static boolean checkOfficers(Board board, int kingFieldID, long free, long officerMoves, PiecesList oppOfficers) {
		int count = oppOfficers.getDataSize();
		int[] data = oppOfficers.getData();
		for (int i=0; i<count; i++) {
			int oppOfficerID = data[i];
			long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
			if ((officerMoves & oppOfficerBitboard) != NUMBER_0) {
				/**
				 * Determine is oppOfficerBitboard makes this potential check.
				 */
				if (checkOfficerDirs(kingFieldID, free, oppOfficerBitboard)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkCastles(Board board, int kingFieldID, long free, long castleMoves, PiecesList oppCastles) {
		int count = oppCastles.getDataSize();
		int[] data = oppCastles.getData();
		for (int i=0; i<count; i++) {
			int oppCastleID = data[i];
			long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
			if ((castleMoves & oppCastleBitboard) != NUMBER_0) {
				/**
				 * Determine is oppCastleBitboard makes this potential check.
				 */
				if (checkCastleDirs(kingFieldID, free, oppCastleBitboard)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean checkOfficerDirs(int kingFieldID, long free, long oppOfficerBitboard) {
		
		long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[kingFieldID];
		if ((dir0 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][0];
			if (checkSlidingDir(free, oppOfficerBitboard, dir0Moves))
				return true;
		}

		long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[kingFieldID];
		if ((dir1 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][1];
			if (checkSlidingDir(free, oppOfficerBitboard, dir1Moves))
				return true;
		}

		long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[kingFieldID];
		if ((dir2 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][2];
			if (checkSlidingDir(free, oppOfficerBitboard, dir2Moves))
				return true;
		}

		long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[kingFieldID];
		if ((dir3 & oppOfficerBitboard) != NUMBER_0) {
			long[] dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][3];
			if (checkSlidingDir(free, oppOfficerBitboard, dir3Moves))
				return true;
		}
		
		return false;
	}

	private static boolean checkCastleDirs(int kingFieldID, long free, long oppCastleBitboard) {
		
		long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[kingFieldID];
		if ((dir0 & oppCastleBitboard) != NUMBER_0) {
			long[] dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][0];
			if (checkSlidingDir(free, oppCastleBitboard, dir0Moves))
				return true;
		}

		long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[kingFieldID];
		if ((dir1 & oppCastleBitboard) != NUMBER_0) {
			long[] dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][1];
			if (checkSlidingDir(free, oppCastleBitboard, dir1Moves))
				return true;
		}

		long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[kingFieldID];
		if ((dir2 & oppCastleBitboard) != NUMBER_0) {
			long[] dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][2];
			if (checkSlidingDir(free, oppCastleBitboard, dir2Moves))
				return true;
		}

		long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[kingFieldID];
		if ((dir3 & oppCastleBitboard) != NUMBER_0) {
			long[] dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][3];
			if (checkSlidingDir(free, oppCastleBitboard, dir3Moves))
				return true;
		}
		
		return false;
	}
	
	private static boolean checkSlidingDir(long free, long oppAttackFigure, long[] dirMoves) {
		/**
		 * The oppOfficerBitboard interacts with dir.
		 * Let's traverse that direction to determine is the check real.
		 */
		
		for (int j=0; j<dirMoves.length; j++) {
			long officerMove = dirMoves[j];
			if ((officerMove & oppAttackFigure) != NUMBER_0) {
				return true;
			}
			if ((officerMove & free) == NUMBER_0) {
				break; //This is figure different from officerMove, there is no interest here.
			}
		}
		
		return false;
	}
	
	/**
	 * Double checks only.
	 */
	public static final boolean isInDoubleCheck(Board board, int colour) {
		boolean result = false;
		
		return result;
	}
	
	public static boolean isCheckMove(Board board, int move, int colour, int opponentColour,
			long free, long opponentKingBitboard, int opponentKingFieldID) {
		if (isDirectCheckMove(move, colour, free, opponentKingBitboard, opponentKingFieldID)) {
			return true;
		} else if (isHiddenCheckMove(board, move, colour, free, opponentKingBitboard, opponentKingFieldID)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isDirectCheckMove(int move, int colour,
			long free, long opponentKingBitboard, int opponentKingFieldID) {
		
		final int toFieldID = MoveInt.getToFieldID(move);
		final int figureType = MoveInt.getFigureType(move);
		
		switch (figureType) {
			case Figures.TYPE_PAWN:
				if (!MoveInt.isPromotion(move)) {
					if (colour == Figures.COLOUR_WHITE) {
						long pawnMoves = WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[toFieldID];
						if ((opponentKingBitboard & pawnMoves) != Bits.NUMBER_0) {
							return true;
						}
					} else {
						long pawnMoves = BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[toFieldID];
						if ((opponentKingBitboard & pawnMoves) != Bits.NUMBER_0) {
							return true;
						}
					}
				} else {
					int promotionFigureType = MoveInt.getPromotionFigureType(move);
					final long fromFieldBoard = Fields.ALL_ORDERED_A1H1[MoveInt.getFromFieldID(move)];
					switch (promotionFigureType) {
						case Figures.TYPE_KNIGHT:
							long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
							if ((opponentKingBitboard & knightMoves) != Bits.NUMBER_0) {
								return true;
							}
							break;
						case Figures.TYPE_OFFICER:
							long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
							if ((opponentKingBitboard & officerMoves) != Bits.NUMBER_0) {
								if (checkOfficerDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) {
									return true;
								}
							}
							break;
						case Figures.TYPE_CASTLE:
							long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
							if ((opponentKingBitboard & castleMoves) != Bits.NUMBER_0) {
								if (checkCastleDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) {
									return true;
								}
							}
							break;
						case Figures.TYPE_QUEEN:
							officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
							if ((opponentKingBitboard & officerMoves) != Bits.NUMBER_0) {
								if (checkOfficerDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) {
									return true;
								}
							}
							castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
							if ((opponentKingBitboard & castleMoves) != Bits.NUMBER_0) {
								if (checkCastleDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) {
									return true;
								}
							}
							break;
					}
				}
				break;
			case Figures.TYPE_KNIGHT:
				long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
				if ((opponentKingBitboard & knightMoves) != Bits.NUMBER_0) {
					return true;
				}
				break;
			case Figures.TYPE_KING:
				long kingMoves = KingPlies.ALL_KING_MOVES[toFieldID];
				if ((opponentKingBitboard & kingMoves) != Bits.NUMBER_0) {
					//throw new IllegalStateException("King gives check: " + Move.moveToString(move));
				}
				
				//Castling check
				if (MoveInt.isCastling(move)) {
					
					int toCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookToFieldID_king(colour) : Castling.getRookToFieldID_queen(colour);

					long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toCastleFieldID];
					if ((opponentKingBitboard & castleMoves) != Bits.NUMBER_0) {
						if (checkCastleDirs(toCastleFieldID, free, opponentKingBitboard)) {
							return true;
						}
					}
				}
				break;
			case Figures.TYPE_OFFICER:
				long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
				if ((opponentKingBitboard & officerMoves) != Bits.NUMBER_0) {
					if (checkOfficerDirs(toFieldID, free, opponentKingBitboard)) {
						return true;
					}
				}
				break;
			case Figures.TYPE_CASTLE:
				long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
				if ((opponentKingBitboard & castleMoves) != Bits.NUMBER_0) {
					if (checkCastleDirs(toFieldID, free, opponentKingBitboard)) {
						return true;
					}
				}
				break;
			case Figures.TYPE_QUEEN:
				officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
				if ((opponentKingBitboard & officerMoves) != Bits.NUMBER_0) {
					if (checkOfficerDirs(toFieldID, free, opponentKingBitboard)) {
						return true;
					}
				}
				castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
				if ((opponentKingBitboard & castleMoves) != Bits.NUMBER_0) {
					if (checkCastleDirs(toFieldID, free, opponentKingBitboard)) {
						return true;
					}
				}
				break;
		}
		
		return false;
	}
	
	public static boolean isHiddenCheckMove(Board board, int move,
			int colour, long free, long opponentKingBitboard,
			int opponentKingFieldID) {

		long fromBoard =  Fields.ALL_ORDERED_A1H1[MoveInt.getFromFieldID(move)];
		final long toBoard = Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)];
		
		if (MoveInt.isEnpassant(move)) {
			long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getEnpassantCapturedFieldID(move)];
			free |= opponentPawnBitboard;
			fromBoard |= opponentPawnBitboard; //To detect the opened field at enpassant
		}
		
		IPiecesLists stateManager = board.getPiecesLists();
		
		//Hidden officers
		long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
		if ((officerMoves & fromBoard) != NUMBER_0) {
			long myOfficersBoard = board.allByColourAndType[colour][Figures.TYPE_OFFICER];
			if ((officerMoves & myOfficersBoard) != NUMBER_0) {
				PiecesList myOfficers = stateManager.getPieces(Figures.getPidByColourAndType(colour, Figures.TYPE_OFFICER));
				if (checkHiddenOfficers(board, opponentKingFieldID, free, officerMoves, myOfficers, fromBoard, toBoard)) {
					return true;
				}
			}
		}

		//Hidden castles
		long castleMoves = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
		if ((castleMoves & fromBoard) != NUMBER_0) {
			long myCastlesBoard = board.allByColourAndType[colour][Figures.TYPE_CASTLE];
			if ((castleMoves & myCastlesBoard) != NUMBER_0) {
				PiecesList myCastles = stateManager.getPieces(Figures.getPidByColourAndType(colour, Figures.TYPE_CASTLE));
				if (checkHiddenCastles(board, opponentKingFieldID, free, castleMoves, myCastles, fromBoard, toBoard)) {
					return true;
				}
			}
		}
		
		//Hidden queens as officers
		long queensOfficerMoves = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
		if ((queensOfficerMoves & fromBoard) != NUMBER_0) {
			long myQueensBoard = board.allByColourAndType[colour][Figures.TYPE_QUEEN];
			if ((queensOfficerMoves & myQueensBoard) != NUMBER_0) {
				PiecesList myQueens = stateManager.getPieces(Figures.getPidByColourAndType(colour, Figures.TYPE_QUEEN));
				if (checkHiddenOfficers(board, opponentKingFieldID, free, queensOfficerMoves, myQueens, fromBoard, toBoard)) {
					return true;
				}
			}
		}

		//Hidden queens as castles
		long queensCastleMoves = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
		if ((queensCastleMoves & fromBoard) != NUMBER_0) {
			long myQueensBoard = board.allByColourAndType[colour][Figures.TYPE_QUEEN];
			if ((queensCastleMoves & myQueensBoard) != NUMBER_0) {
				PiecesList myQueens = stateManager.getPieces(Figures.getPidByColourAndType(colour, Figures.TYPE_QUEEN));
				if (checkHiddenCastles(board, opponentKingFieldID, free, queensCastleMoves, myQueens, fromBoard, toBoard)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean checkHiddenOfficers(Board board, int kingFieldID,
			long free, long officerMoves, PiecesList oppOfficers,
			long unstopperFromBoard, long unstopperToBoard) {
		
		int count = oppOfficers.getDataSize();
		int[] data = oppOfficers.getData();
		for (int i=0; i<count; i++) {
			int oppOfficerID = data[i];
			long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
			if ((officerMoves & oppOfficerBitboard) != NUMBER_0
					&& (officerMoves & unstopperFromBoard) != NUMBER_0) {
				/**
				 * Determine is oppOfficerBitboard makes this potential check.
				 */
				if (checkHiddenOfficerDirs(kingFieldID, free, oppOfficerBitboard,
						unstopperFromBoard, unstopperToBoard)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkHiddenCastles(Board board, int opponentKingFieldID,
			long free, long castleMoves, PiecesList oppCastles,
			long unstopperFromBoard, long unstopperToBoard) {
		
		int count = oppCastles.getDataSize();
		int[] data = oppCastles.getData();
		for (int i=0; i<count; i++) {
			int oppCastleID = data[i];
			long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
			if ((castleMoves & oppCastleBitboard) != NUMBER_0
					&& (castleMoves & unstopperFromBoard) != NUMBER_0) {
				/**
				 * Determine is oppCastleBitboard makes this potential check.
				 */
				if (checkHiddenCastleDirs(opponentKingFieldID, free, oppCastleBitboard,
						unstopperFromBoard, unstopperToBoard)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean checkHiddenOfficerDirs(int opponentKingFieldID, long free, long oppOfficerBitboard,
			long unstopperFromBoard, long unstopperToBoard) {
		
		long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[opponentKingFieldID];
		
		if ((dir0 & oppOfficerBitboard) != NUMBER_0
				&& (dir0 & unstopperFromBoard) != NUMBER_0
				&& (dir0 & unstopperToBoard) == NUMBER_0) {
			long[] dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][0];
			if (checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir0Moves))
				return true;
		}

		long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[opponentKingFieldID];
		if ((dir1 & oppOfficerBitboard) != NUMBER_0
				&& (dir1 & unstopperFromBoard) != NUMBER_0
				&& (dir1 & unstopperToBoard) == NUMBER_0) {
			long[] dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][1];
			if (checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir1Moves))
				return true;
		}

		long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[opponentKingFieldID];
		if ((dir2 & oppOfficerBitboard) != NUMBER_0
				&& (dir2 & unstopperFromBoard) != NUMBER_0
				&& (dir2 & unstopperToBoard) == NUMBER_0) {
			long[] dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][2];
			if (checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir2Moves))
				return true;
		}

		long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[opponentKingFieldID];
		if ((dir3 & oppOfficerBitboard) != NUMBER_0
				&& (dir3 & unstopperFromBoard) != NUMBER_0
				&& (dir3 & unstopperToBoard) == NUMBER_0) {
			long[] dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][3];
			if (checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir3Moves))
				return true;
		}
		
		return false;
	}

	private static boolean checkHiddenCastleDirs(int opponentKingFieldID, long free, long oppCastleBitboard,
			long unstopperFromBoard, long unstopperToBoard) {
		
		long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[opponentKingFieldID];
		if ((dir0 & oppCastleBitboard) != NUMBER_0
				&& (dir0 & unstopperFromBoard) != NUMBER_0
				&& (dir0 & unstopperToBoard) == NUMBER_0) {
			long[] dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][0];
			if (checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir0Moves))
				return true;
		}

		long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[opponentKingFieldID];
		if ((dir1 & oppCastleBitboard) != NUMBER_0
				&& (dir1 & unstopperFromBoard) != NUMBER_0
				&& (dir1 & unstopperToBoard) == NUMBER_0) {
			long[] dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][1];
			if (checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir1Moves))
				return true;
		}

		long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[opponentKingFieldID];
		if ((dir2 & oppCastleBitboard) != NUMBER_0
				&& (dir2 & unstopperFromBoard) != NUMBER_0
				&& (dir2 & unstopperToBoard) == NUMBER_0) {
			long[] dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][2];
			if (checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir2Moves))
				return true;
		}

		long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[opponentKingFieldID];
		if ((dir3 & oppCastleBitboard) != NUMBER_0
				&& (dir3 & unstopperFromBoard) != NUMBER_0
				&& (dir3 & unstopperToBoard) == NUMBER_0) {
			long[] dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][3];
			if (checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir3Moves))
				return true;
		}
		
		return false;
	}
}
