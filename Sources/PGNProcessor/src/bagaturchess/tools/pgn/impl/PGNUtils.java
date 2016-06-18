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
package bagaturchess.tools.pgn.impl;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
//import bagaturchess.bitboard.impl1.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public class PGNUtils implements PGNConstants {

	public static int translatePGNMove(IBoard bitboard, int colour, String pPGNTurn, boolean validateChechAndMate) {
		int result = 0;
		
		int length = pPGNTurn.length();
		//		if ( length < 2 || length > 7 ) {
		if (length < 1 || length > 7) {
			throw new IllegalArgumentException(
				"Incorrect size of PGN turn:'" + pPGNTurn + "'.");
		}
		//((TurnsReferenceIterator) getPossibleTurns()).goBeforeFirst();
		if (pPGNTurn.startsWith(SAN_CASTLE_QUEEN_SIDE_STR)) {
			result = makeCastleQueenSide(bitboard, colour, pPGNTurn);
		} else if (pPGNTurn.startsWith(SAN_CASTLE_KING_SIDE_STR)) {
			result = makeCastleKingSide(bitboard, colour, pPGNTurn);
		} else { //Turn is not castle side
			result = makeTurnBySAN(bitboard, colour, pPGNTurn, validateChechAndMate);
		}
		return result;
	}

	public static int makeCastleKingSide(IBoard bitboard, int colour, String pPGNTurn) {
		
		int turnToMove = 0;
		
		boolean founded = false;
		
		IMoveList mlist = new BaseMoveList();
		bitboard.genAllMoves(mlist);
		
		int cur_move = 0;
		while ((cur_move = mlist.next()) != 0) {
			if (MoveInt.isCastleKingSide(cur_move)) {
				if (!founded) {
					founded = true;
					turnToMove = cur_move;
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		if (!founded) {
			throw new IllegalStateException();
		}
		
		return turnToMove;
	}

	public static int makeCastleQueenSide(IBoard bitboard, int colour, String pPGNTurn) {
		
		int turnToMove = 0;
		
		boolean founded = false;
		
		IMoveList mlist = new BaseMoveList();
		bitboard.genAllMoves(mlist);
		
		int cur_move = 0;
		while ((cur_move = mlist.next()) != 0) {
			if (MoveInt.isCastleQueenSide(cur_move)) {
				if (!founded) {
					founded = true;
					turnToMove = cur_move;
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		if (!founded) {
			
			mlist.clear();
			bitboard.genAllMoves(mlist);
			
			String all_moves = "";
			int move = 0;
			while ((move = mlist.next()) != 0) {
				all_moves += bagaturchess.bitboard.impl1.movegen.MoveInt.moveToString(move);
				all_moves += ", ";
			}
			
			throw new IllegalStateException(all_moves);
		}
		
		return turnToMove;
	}
	

	public static int makeTurnBySAN(IBoard bitboard, int colour, String pPGNTurn, boolean validateChechAndMate) {
		int turnToMove = 0;
		
		boolean isChess = false;
		boolean isMate = false;
		boolean isPromotion = false;
		boolean isKiller = false;
		int promotionFigureType = Figures.TYPE_UNDEFINED;
		int fromLetter = SAN_FILE_UNDEFINED;
		int fromDigit = SAN_RANK_UNDEFINED;
		int toLetter = SAN_FILE_UNDEFINED;
		int toDigit = SAN_RANK_UNDEFINED;
		int figureType = Figures.TYPE_PAWN;

		int size = pPGNTurn.length();
		int index = size - 1;

		char symbol = pPGNTurn.charAt(index);
		if (symbol == SAN_CHECK_SUFFIX_CHAR) {
			isChess = true;
			index--;
			symbol = pPGNTurn.charAt(index);
		}
		if (symbol == SAN_MATE_SUFFIX_CHAR) {
			isMate = true;
			index--;
			symbol = pPGNTurn.charAt(index);
		}
		if (isIdentifierCharacter(symbol)) {
			isPromotion = true;
			promotionFigureType = getSoldierType(symbol);
			index -= 2;
			symbol = pPGNTurn.charAt(index);
		}
		if (isRankCharacter(symbol)) {
			toDigit = getRankIdentifier(symbol);
			index--;
			symbol = pPGNTurn.charAt(index);
		} /*else {
				 throw new InvalidPGNFormatException( "Expected rank of destination square is missing! PGN turn:'" + pPGNTurn + "'" );
			 }*/
		if (isFileCharacter(symbol)) {
			toLetter = getFileIdentifier(symbol);
			index--;
		} /*else {
				 throw new InvalidPGNFormatException( "Expected file of destination square is missing! PGN turn:'" + pPGNTurn + "'" );
			 }*/
		if (index >= 0) {
			symbol = pPGNTurn.charAt(index);
			if (symbol == SAN_CAPTURE_CHAR) {
				isKiller = true;
				index--;
			}
		}
		if (index >= 0) {
			symbol = pPGNTurn.charAt(index);
			if (isRankCharacter(symbol)) {
				fromDigit = getRankIdentifier(symbol);
				index--;
			}
		}
		if (index >= 0) {
			symbol = pPGNTurn.charAt(index);
			if (isFileCharacter(symbol)) {
				fromLetter = getFileIdentifier(symbol);
				index--;
			}
		}
		if (index >= 0) {
			symbol = pPGNTurn.charAt(index);
			if (isIdentifierCharacter(symbol)
				&& symbol != SAN_IDENTIFIER_PAWN_CHAR) {
				figureType = getSoldierType(symbol);
			}
		}

		boolean founded = false;
		
		IMoveList mlist = new BaseMoveList(150);
		bitboard.genAllMoves(mlist);
		
		int cur_move = 0;
		while ((cur_move = mlist.next()) != 0) {

			int soldierType = MoveInt.getFigureType(cur_move);

			int producedSoldierType = MoveInt.getPromotionFigureType(cur_move);
			if (figureType == soldierType
				&& (fromLetter == Fields.LETTERS[MoveInt.getFromFieldID(cur_move)]
					|| fromLetter == SAN_FILE_UNDEFINED)
				&& (fromDigit == Fields.DIGITS[MoveInt.getFromFieldID(cur_move)]
					|| fromDigit == SAN_RANK_UNDEFINED)
				&& (toLetter == Fields.LETTERS[MoveInt.getToFieldID(cur_move)]
					|| toLetter == SAN_FILE_UNDEFINED)
				&& (toDigit == Fields.DIGITS[MoveInt.getToFieldID(cur_move)]
					|| toDigit == SAN_RANK_UNDEFINED)
				&& isKiller == MoveInt.isCapture(cur_move)
				&& (isPromotion == MoveInt.isPromotion(cur_move))
				&& ((isPromotion == true && promotionFigureType == producedSoldierType) || isPromotion == false)
				//&& (!validateChechAndMate
					//	|| (validateChechAndMate && (isChess == turn.isCheck() || isMate == turn.isCheck())))
				) {
				if (!founded) {
					founded = true;
					turnToMove = cur_move;
				} else {
					
					throw new IllegalArgumentException(
						"Duplicate turn '"
							+ pPGNTurn
							+ "' found!\r\n"
							+ " PlayerColour="
							+ colour
							+ "\r\n"
							+ " Current game matrix is: "
							+ bitboard
							+ "turn1="
							+ turnToMove
							+ "\r\n"
							+ "turn2="
							//+ MoveInt.moveToString(pv)
							+ "\r\n");
							//+ getTurnsAsString(game.getCurrentlyPossibleTurns(colour).iterator()));
				}
			}
		}
		
		if (!founded) {
			
			mlist.clear();
			bitboard.genAllMoves(mlist);
			
			String all_moves = "";
			int move = 0;
			while ((move = mlist.next()) != 0) {
				all_moves += bagaturchess.bitboard.impl1.movegen.MoveInt.moveToString(move);
				all_moves += ", ";
			}
			
			throw new IllegalArgumentException(
				" Turn '"
					+ pPGNTurn
					+ "' not found!"
					+ " PlayerColour="
					+ colour
					+ "\n"
					+ "GEN MOVES: " + all_moves
					+ "\r\n"
					+ " Current game is: "
					+ bitboard ); 
					//+ getTurnsAsString(game.getCurrentlyPossibleTurns(colour).iterator()) );
		}
		return turnToMove;
	}

	/*private static String getTurnsAsString(Iterator aIter) {
		String result = "\r\nTURNS START\r\n";
		//((TurnsReferenceIterator) aIter).goBeforeFirst();
		while (aIter.hasNext()) {
			IMove turn = (IMove) aIter.next();
			result += turn + "\r\n";
		}
		result += "\r\nTURNS END\r\n";
		return result;
	}*/

	private static final boolean isFileCharacter(char pChar) {
		return (
			pChar == SAN_FILE_A_CHAR
				|| pChar == SAN_FILE_B_CHAR
				|| pChar == SAN_FILE_C_CHAR
				|| pChar == SAN_FILE_D_CHAR
				|| pChar == SAN_FILE_E_CHAR
				|| pChar == SAN_FILE_F_CHAR
				|| pChar == SAN_FILE_G_CHAR
				|| pChar == SAN_FILE_H_CHAR);
	}

	public static final int getFileIdentifier(char pChar) {
		
		int result = SAN_FILE_UNDEFINED;
		
		if (pChar == SAN_FILE_A_CHAR) {
			result = Fields.LETTER_A_ID;
		} else if (pChar == SAN_FILE_B_CHAR) {
			result = Fields.LETTER_B_ID;
		} else if (pChar == SAN_FILE_C_CHAR) {
			result = Fields.LETTER_C_ID;
		} else if (pChar == SAN_FILE_D_CHAR) {
			result = Fields.LETTER_D_ID;
		} else if (pChar == SAN_FILE_E_CHAR) {
			result = Fields.LETTER_E_ID;
		} else if (pChar == SAN_FILE_F_CHAR) {
			result = Fields.LETTER_F_ID;
		} else if (pChar == SAN_FILE_G_CHAR) {
			result = Fields.LETTER_G_ID;
		} else if (pChar == SAN_FILE_H_CHAR) {
			result = Fields.LETTER_H_ID;
		} else {
			throw new IllegalStateException();
		}
		return result;
	}

	private static final boolean isRankCharacter(char pChar) {
		return (
			pChar == SAN_RANK_1_CHAR
				|| pChar == SAN_RANK_2_CHAR
				|| pChar == SAN_RANK_3_CHAR
				|| pChar == SAN_RANK_4_CHAR
				|| pChar == SAN_RANK_5_CHAR
				|| pChar == SAN_RANK_6_CHAR
				|| pChar == SAN_RANK_7_CHAR
				|| pChar == SAN_RANK_8_CHAR);
	}

	public static final int getRankIdentifier(char pChar) {
		
		int result = SAN_RANK_UNDEFINED;
		
		if (pChar == SAN_RANK_1_CHAR) {
			result = Fields.DIGIT_1_ID;
		} else if (pChar == SAN_RANK_2_CHAR) {
			result = Fields.DIGIT_2_ID;
		} else if (pChar == SAN_RANK_3_CHAR) {
			result = Fields.DIGIT_3_ID;
		} else if (pChar == SAN_RANK_4_CHAR) {
			result = Fields.DIGIT_4_ID;
		} else if (pChar == SAN_RANK_5_CHAR) {
			result = Fields.DIGIT_5_ID;
		} else if (pChar == SAN_RANK_6_CHAR) {
			result = Fields.DIGIT_6_ID;
		} else if (pChar == SAN_RANK_7_CHAR) {
			result = Fields.DIGIT_7_ID;
		} else if (pChar == SAN_RANK_8_CHAR) {
			result = Fields.DIGIT_8_ID;
		}
		return result;
	}

	private static final boolean isIdentifierCharacter(char pChar) {
		return (
			pChar == SAN_IDENTIFIER_BISHOP_CHAR
				|| pChar == SAN_IDENTIFIER_KING_CHAR
				|| pChar == SAN_IDENTIFIER_KNIGHT_CHAR
				|| pChar == SAN_IDENTIFIER_PAWN_CHAR
				|| pChar == SAN_IDENTIFIER_QUEEN_CHAR
				|| pChar == SAN_IDENTIFIER_ROOK_CHAR);
	}

	private static final int getSoldierType(char pChar) {
		
		int result = Figures.TYPE_PAWN;
		
		if (pChar == SAN_IDENTIFIER_BISHOP_CHAR) {
			result = Figures.TYPE_OFFICER;
		} else if (pChar == SAN_IDENTIFIER_KING_CHAR) {
			result = Figures.TYPE_KING;
		} else if (pChar == SAN_IDENTIFIER_KNIGHT_CHAR) {
			result = Figures.TYPE_KNIGHT;
		} else if (pChar == SAN_IDENTIFIER_QUEEN_CHAR) {
			result = Figures.TYPE_QUEEN;
		} else if (pChar == SAN_IDENTIFIER_ROOK_CHAR) {
			result = Figures.TYPE_CASTLE;
		}
		
		return result;
	}
}
