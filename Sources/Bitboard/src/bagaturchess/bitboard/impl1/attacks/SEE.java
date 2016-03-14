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
package bagaturchess.bitboard.impl1.attacks;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl1.movegen.MoveInt;


public class SEE implements ISEE {
	
	
	private static final boolean STOP_AT_QUEEN_ATTACK = false;
	private IBoard bitboard;

	private boolean[] canBeCaptured = new boolean[1];
	private int[] buff = new int[3];
	
	
	public SEE(IBoard _bitboard) {
		bitboard = _bitboard;
	}
	
	public int evalExchange(int move) {
		return evalExchange(move, canBeCaptured);
	}
	
	public int evalExchange(int move, boolean[] canBeCaptured) {
		//if (true) return 0;
		
		/*if (!bitboard.getAttacksSupport()) {
			//return 0;
			throw new IllegalStateException();
		}*/
		
		int countAttacks = 0;
		
		int value = 0;
		
		//int factor = bitboard.getBaseEvaluation().getTotalFactor();
		
		if (MoveInt.isCastling(move)) {
			return getCost(Figures.TYPE_PAWN);
		}
		
		if (MoveInt.isEnpassant(move)) {
			return getCost(Figures.TYPE_PAWN);
		}
		
		/**
		 * Here, move is nor enpassant nor castle side
		 */
		
		/**
		 * First start with the capture and promotion evals of the move
		 */
		boolean initialPawnMove = MoveInt.isPawn(move);
		boolean initialPawnCapture = false;
		if (MoveInt.isCaptureOrPromotion(move)) {
			//int toFieldID = Move.getToFieldID(move);
			if (MoveInt.isCapture(move)) {
				int capFigType = MoveInt.getCapturedFigureType(move);
				value += getCost(capFigType);
				initialPawnCapture = initialPawnMove;
				//seqence += " [" + Figures.TYPES_SIGN[Move.getFigureType(move)] + " +" + cost(capFigType) + "]";
			}
			countAttacks++;
		}
		
		int oppColour = MoveInt.getOpponentColour(move);
		long toFieldBitboard = MoveInt.getToFieldBitboard(move);
		
		//IPlayerAttacks opAttacks = bitboard.getPlayerAttacks(oppColour);
		
		/**
		 * To make possible running dummy board
		 */
		/*if (opAttacks == null) {
			return 0;
		}*/
		
		/**
		 * My move is evaluated, so start with the opponent attacks if any
		 */
		//long opAll = opAttacks.allAttacks();
		//if ((opAll & toFieldBitboard) != 0L) {
			
			//Here, there is at least one attack because of the if statement above
			
			int figType = MoveInt.getFigureType(move);
			//int figCost = getCost(figType);
			
			/**
			 * Winning capture and/or promotion
			 * Return pesimistic value - do not care about the value of capturing piece.
			 */
			/*int pesVal = value - figCost;
			if (pesVal > 0) {
				return pesVal;
			}*/
			
			int colour = MoveInt.getColour(move);
			//IPlayerAttacks myAttacks = bitboard.getPlayerAttacks(colour);
			
			
			//Determine skip type
			int skipType = Figures.TYPE_UNDEFINED;
			//int startIndex = 0;
			if (initialPawnMove) {
				if (initialPawnCapture) {
					skipType = figType;//Figures.TYPE_PAWN;
				} else {
					//pawn moved forward -> there is no need to exclude(skip) its attack because it is not presented
					//throw new IllegalStateException();
				}
			} else {
				skipType = figType;
			}
			
			/*
			//Build attacks lists
			int myAttacksCount = buildAttacksList(toFieldBitboard, myAttacks, myAttacksList, 0, skipType);
			int opAttacksCount = buildAttacksList(toFieldBitboard, opAttacks, opAttacksList, 0, Figures.TYPE_UNDEFINED);
			
			int see = -see(figCost, 0, opAttacksCount, opAttacksList, 0, myAttacksCount, myAttacksList);
			value += see;*/
			
			int toFieldID = MoveInt.getToFieldID(move);
			
			int c_state = -1;//bitboard.getFieldsAttacks().getControlArray(colour)[toFieldID];
			int oc_state = -1;//bitboard.getFieldsAttacks().getControlArray(oppColour)[toFieldID];
			
			/*if (bitboard.getFieldsStateSupport()) {
				c_state = bitboard.getFieldsAttacks().getControlArray(colour)[toFieldID];
				oc_state = bitboard.getFieldsAttacks().getControlArray(oppColour)[toFieldID];
			} // else {
				*/
				int[] matrix = bitboard.getMatrix();
				
				//c_state = colour == Figures.COLOUR_WHITE ? buildAttacksList(false, Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(false, Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
				//oc_state = oppColour == Figures.COLOUR_WHITE ? buildAttacksList(false, Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(false, Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
			
				c_state = colour == Figures.COLOUR_WHITE ? getFieldState(toFieldID, Figures.COLOUR_WHITE, matrix) : getFieldState(toFieldID, Figures.COLOUR_BLACK, matrix);
				oc_state = oppColour == Figures.COLOUR_WHITE ? getFieldState(toFieldID, Figures.COLOUR_WHITE, matrix) : getFieldState(toFieldID, Figures.COLOUR_BLACK, matrix);
			
				
				
				//buildAttacksList(toFieldID, toFieldBitboard);
				/*if (w_state != buildAttacksList(Figures.COLOUR_WHITE, toFieldID, toFieldBitboard)) {
					String msg = "w_state=" + FieldAttacksStateMachine.getInstance().getAllStatesList()[w_state]
                        + ", state=" + FieldAttacksStateMachine.getInstance().getAllStatesList()[buildAttacksList(Figures.COLOUR_WHITE, toFieldID, toFieldBitboard)];
					throw new IllegalStateException(msg);
				}
				if (b_state != buildAttacksList(Figures.COLOUR_BLACK, toFieldID, toFieldBitboard)) {
					String msg = "b_state=" + FieldAttacksStateMachine.getInstance().getAllStatesList()[b_state]
                 + ", state=" + FieldAttacksStateMachine.getInstance().getAllStatesList()[buildAttacksList(Figures.COLOUR_BLACK, toFieldID, toFieldBitboard)];
          throw new IllegalStateException(msg);
				}*/
			//}
			
			
			int test = 0;
			//try {
				test = SeeMetadata.getSingleton().seeMove(skipType, figType, c_state, oc_state);
			//} catch (RuntimeException e) {
				 //buildAttacksListByAttacks(Figures.COLOUR_WHITE, toFieldBitboard);
				// buildAttacksListByAttacks(Figures.COLOUR_BLACK, toFieldBitboard);
				// throw e;
			//}
				
			if (MoveInt.isPromotion(move)) {
				if (test == 0) {
					int promFigType = MoveInt.getPromotionFigureType(move);
					value += (getCost(promFigType) - getCost(Figures.TYPE_PAWN));
				} else {
					if (test != 100 && test != 75) {
						//throw new IllegalStateException("test=" + test);
					}
				}
			}
				
			value -= test;
			
			/*if (see != test) {
				System.out.println("see=" + see + ", test=" + test);
			}*/
			
			/*if (value != 0) {
				int toFieldID = Move.getToFieldID(move);
				String message = bitboard + "\r\n"; 
				String id = Fields.getFieldSign_UC(toFieldID) + " -> " + Move.moveToString(move) + " " + value;
				message += id;
				//message +=  + "		" + seqence;
				System.out.println(message);
				message += "";
				/*if (id.startsWith("C5 -> c7-c5(0)")) {
					evalExchange(move);
					System.exit(0);				
				}*/
			//}
		//}
		
		return value;
	}
	
	
	public int seeField(int fieldID) {
		
		//if (true) throw new IllegalStateException("Check calls to SEE");
		
		int figurePID = bitboard.getFigureID(fieldID);
		int figureColour = Figures.getFigureColour(figurePID);
		int figureType = Figures.getFigureType(figurePID);
		
		long fieldBoard = Fields.ALL_A1H1[fieldID];
		
		int w_state = 0;
		int b_state = 0;
		
		buff[Figures.COLOUR_WHITE] = 0;
		buff[Figures.COLOUR_BLACK] = 0;
		
		buildAttacksList(fieldID, fieldBoard, buff);
		
		w_state = buff[Figures.COLOUR_WHITE];
		b_state = buff[Figures.COLOUR_BLACK];
		
		
		int result = -SeeMetadata.getSingleton().seeMove(Figures.TYPE_UNDEFINED, figureType,
				figureColour == Figures.COLOUR_WHITE ? w_state : b_state,
				figureColour == Figures.COLOUR_WHITE ? b_state : w_state);
		
		return result;
	}
	
	public int seeMove(int pieceColour, int pieceType, int toFiledID) {
		
		//if (true) throw new IllegalStateException("Check calls to SEE");
		
		int w_state = 0;
		int b_state = 0;

		buff[Figures.COLOUR_WHITE] = 0;
		buff[Figures.COLOUR_BLACK] = 0;
		
		long fieldBoard = Fields.ALL_A1H1[toFiledID];
		buildAttacksList(toFiledID, fieldBoard, buff);
		
		w_state = buff[Figures.COLOUR_WHITE];
		b_state = buff[Figures.COLOUR_BLACK];
		
		int result = -SeeMetadata.getSingleton().seeMove(pieceType, pieceType,
				pieceColour == Figures.COLOUR_WHITE ? w_state : b_state,
				pieceColour == Figures.COLOUR_WHITE ? b_state : w_state);
		
		return result;
	}
	
	
	private static int nextState(int currentState, int figureType) {
		int next = FieldAttacksStateMachine.getInstance().getMachine()[IFieldsAttacks.OP_ADD_ATTACK][figureType][currentState];
		if (next == -1) {
			throw new IllegalStateException("Try to add attack from "
					+ Figures.TYPES_SIGN[figureType] + " to state " + FieldAttacksStateMachine.getInstance().getAllStatesList()[currentState]);
		}
		return next;
	}
	
	private static int nextState(int currentState, int figureType, int count) {
		for (int i=0; i<count; i++) {
			currentState = nextState(currentState, figureType);
		}
		return currentState;
	}
	
	
	//private int buildAttacksList(boolean stop, int colour, int toFieldID, long toFieldBitboard) {
	public static final int getFieldState(final int fieldID, final int attackingColour, final int[] board) {
		
		
		int state = 0;
		
		//Check for rook and queen attacks
		int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
		int[][] dirs_ids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int[] dirIDs = dirs_ids[dirID];
			
			for (int seq=0; seq<dirIDs.length; seq++) {
				
				final int toFieldID = dirIDs[seq];
				final int targetPID = board[toFieldID];
				if (targetPID != 0) {
					
					int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_QUEEN : Constants.PID_B_QUEEN;
					if (targetPID == expectedPID) {
						state = nextState(state, Figures.TYPE_QUEEN);
					}
					
					expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_ROOK : Constants.PID_B_ROOK;
					if (targetPID == expectedPID) {
						state = nextState(state, Figures.TYPE_CASTLE);
					}
					
					break; //Stop search in this direction
				}
			}
		}
		
		
		//Check for officer and queen attacks
		validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
		dirs_ids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int[] dirIDs = dirs_ids[dirID];
			
			for (int seq=0; seq<dirIDs.length; seq++) {
				
				final int toFieldID = dirIDs[seq];
				final int targetPID = board[toFieldID];
				if (targetPID != 0) {
					
					int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_QUEEN : Constants.PID_B_QUEEN;
					if (targetPID == expectedPID) {
						state = nextState(state, Figures.TYPE_QUEEN);
					}
					
					expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_BISHOP : Constants.PID_B_BISHOP;
					if (targetPID == expectedPID) {
						state = nextState(state, Figures.TYPE_OFFICER);
					}
					
					break; //Stop search in this direction
				}
			}
		}
		
		
		//Check for knight attacks
		validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
		dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int toFieldID = dirs_ids[dirID][0];
			final int targetPID = board[toFieldID];
			
			if (targetPID != 0) {
				final int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_KNIGHT : Constants.PID_B_KNIGHT;
				if (targetPID == expectedPID) {
					state = nextState(state, Figures.TYPE_KNIGHT);
				}
			}
		}
		
		
		//Check for king attacks
		validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fieldID];
		dirs_ids = KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[fieldID];
		size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int toFieldID = dirs_ids[dirID][0];
			final int targetPID = board[toFieldID];
			
			if (targetPID != 0) {
				final int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_KING : Constants.PID_B_KING;
				if (targetPID == expectedPID) {
					state = nextState(state, Figures.TYPE_KING);
				}
			}
		}
		
		
		//Check for pawn attacks
		switch (attackingColour) {
		
			case Constants.COLOUR_WHITE:
			{
				int letter = Fields.LETTERS[fieldID];
				if (letter != Fields.LETTER_A_ID) {
					final int targetFieldID = fieldID - 9;
					if (targetFieldID >= 0) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_W_PAWN) {
							state = nextState(state, Figures.TYPE_PAWN);
						}
					}
				}
				
				if (letter != Fields.LETTER_H_ID) {
					final int targetFieldID = fieldID - 7;
					if (targetFieldID >= 0) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_W_PAWN) {
							state = nextState(state, Figures.TYPE_PAWN);
						}
					}
				}
				
				break;
			}
			case Constants.COLOUR_BLACK:
			{
				int letter = Fields.LETTERS[fieldID];
				if (letter != Fields.LETTER_A_ID) {
					final int targetFieldID = fieldID + 7;
					if (targetFieldID <= 63) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_B_PAWN) {
							state = nextState(state, Figures.TYPE_PAWN);
						}
					}
				}
				
				if (letter != Fields.LETTER_H_ID) {
					final int targetFieldID = fieldID + 9;
					if (targetFieldID <= 63) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_B_PAWN) {
							state = nextState(state, Figures.TYPE_PAWN);
						}
					}
				}
				
				break;
			}
		}
		
		//System.out.println("state=" + state);
		
		return state;
	}
	
	
	private int buildAttacksList(boolean stop, int colour, int toFieldID, long toFieldBitboard) {
		
		//if (true) throw new IllegalStateException("Check calls to SEE");
		
		int state = 0;
		
		//long free = bitboard.getFreeBitboard();
		
		//Check pawns attacks
		long pawns = 0;
		if (colour == Figures.COLOUR_WHITE) {
			pawns = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_PAWN]);
			{
				long potentialWPawnsPlaces = 0;
				if ((Fields.LETTER_A & toFieldBitboard) == Figures.NUMBER_0 ) {
					potentialWPawnsPlaces |= toFieldBitboard << 9;
				}
				if ((Fields.LETTER_H & toFieldBitboard) == Figures.NUMBER_0 ) {
					potentialWPawnsPlaces |= toFieldBitboard << 7;
				}
				
				if ((pawns & potentialWPawnsPlaces) != Figures.NUMBER_0) {
					state = nextState(state, Figures.TYPE_PAWN, Utils.countBits(pawns & potentialWPawnsPlaces));
				}
			}
		} else {
			pawns = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_PAWN]);
			{
				long potentialBPawnsPlaces = 0;
				if ((Fields.LETTER_A & toFieldBitboard) == Figures.NUMBER_0 ) {
					potentialBPawnsPlaces |= toFieldBitboard >>> 7;
				}
				if ((Fields.LETTER_H & toFieldBitboard) == Figures.NUMBER_0 ) {
					potentialBPawnsPlaces |= toFieldBitboard >>> 9;
				}
				
				if ((pawns & potentialBPawnsPlaces) != Figures.NUMBER_0) {
					state = nextState(state, Figures.TYPE_PAWN, Utils.countBits(pawns & potentialBPawnsPlaces));
				}
			}
		}
		
		if (stop && state > 0) {
			return state;
		}
		
		//Check knights
		long potentialKnightsPlaces = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
		{
			long knights = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_KNIGHT]);
			if ((knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				state = nextState(state, Figures.TYPE_KNIGHT, Utils.countBits(knights & potentialKnightsPlaces));
			}
		}
		
		if (stop && state > 0) {
			return state;
		}
		
		long queens = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_QUEEN]);
		{
			//Officers
			long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
			
			//White officers
			long officers = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_OFFICER]);
			long officer_sliders = officers | queens;
			if ((officer_sliders & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
					if ((officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
						int[] dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((officerMove & officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								if (colour == Figures.COLOUR_BLACK && j == 0 && (pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID];
					if ((officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
						int[] dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((officerMove & officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								if (colour == Figures.COLOUR_WHITE && j == 0 && (pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID];
					if ((officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
						int[] dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((officerMove & officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								if (colour == Figures.COLOUR_WHITE && j == 0 && (pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID];
					if ((officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
						int[] dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((officerMove & officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								if (colour == Figures.COLOUR_BLACK && j == 0 && (pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
			}
		}
		
		if (stop && state > 0) {
			return state;
		}
		
		{
			//Castles
			long potentialCastlesPlaces = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
			
			//White officers
			long castles = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_CASTLE]);
			long castle_sliders = castles | queens;
			if ((castle_sliders & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
					if ((castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
						int[] dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((castleMove & castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID];
					if ((castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
						int[] dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((castleMove & castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID];
					if ((castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
						int[] dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((castleMove & castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID];
					if ((castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
						int[] dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							int fieldID = dirIDs[j];
							if ((castleMove & castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									state = nextState(state, Figures.TYPE_QUEEN);
								} else {
									state = nextState(state, Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & queens) != Figures.NUMBER_0) {
								state = nextState(state, Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if (bitboard.getFigureID(fieldID) != Constants.PID_NONE) {
								break;
							}
						}
					}
				}
			}
		}
		
		if (stop && state > 0) {
			return state;
		}
		
		{
			//Check opponent king
			long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
			long king = bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][Figures.TYPE_KING]);
			if ((king & potentialKingPlaces) != Figures.NUMBER_0) {
				state = nextState(state, Figures.TYPE_KING);
			}
		}
		
		return state;
	}
	
	private void buildAttacksList(int toFieldID, long toFieldBitboard, int[] states) {
		
		//if (true) throw new IllegalStateException("Check calls to SEE");
		
		//long w_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
		//long b_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);
		long free = bitboard.getFreeBitboard();
		
		//Check knights
		long potentialKnightsPlaces = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
		{
			long w_knights = bitboard.getFiguresBitboardByPID(Constants.PID_W_KNIGHT);
			if ((w_knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_KNIGHT, Utils.countBits(w_knights & potentialKnightsPlaces));
			}
			long b_knights = bitboard.getFiguresBitboardByPID(Constants.PID_B_KNIGHT);
			if ((b_knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_KNIGHT, Utils.countBits(b_knights & potentialKnightsPlaces));
			}
		}
		
		{
			//Check opponent king
			long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
			long w_king = bitboard.getFiguresBitboardByPID(Constants.PID_W_KING);
			if ((w_king & potentialKingPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_KING);
			}
			long b_king = bitboard.getFiguresBitboardByPID(Constants.PID_B_KING);
			if ((b_king & potentialKingPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_KING);
			}
		}
		
		//Check pawns attacks
		long w_pawns = bitboard.getFiguresBitboardByPID(Constants.PID_W_PAWN);
		{
			long potentialWPawnsPlaces = 0;
			if ((Fields.LETTER_A & toFieldBitboard) == Figures.NUMBER_0 ) {
				potentialWPawnsPlaces |= toFieldBitboard << 9;
			}
			if ((Fields.LETTER_H & toFieldBitboard) == Figures.NUMBER_0 ) {
				potentialWPawnsPlaces |= toFieldBitboard << 7;
			}
			
			if ((w_pawns & potentialWPawnsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_PAWN, Utils.countBits(w_pawns & potentialWPawnsPlaces));
			}
		}
		long b_pawns = bitboard.getFiguresBitboardByPID(Constants.PID_B_PAWN);
		{
			long potentialBPawnsPlaces = 0;
			if ((Fields.LETTER_A & toFieldBitboard) == Figures.NUMBER_0 ) {
				potentialBPawnsPlaces |= toFieldBitboard >>> 7;
			}
			if ((Fields.LETTER_H & toFieldBitboard) == Figures.NUMBER_0 ) {
				potentialBPawnsPlaces |= toFieldBitboard >>> 9;
			}
			
			if ((b_pawns & potentialBPawnsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_PAWN, Utils.countBits(b_pawns & potentialBPawnsPlaces));
			}
		}
		
		long w_queens = bitboard.getFiguresBitboardByPID(Constants.PID_W_QUEEN);
		long b_queens = bitboard.getFiguresBitboardByPID(Constants.PID_B_QUEEN);
		
		{
			//Officers
			long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
			
			//White officers
			long w_officers = bitboard.getFiguresBitboardByPID(Constants.PID_W_BISHOP);
			long w_officer_sliders = w_officers | w_queens;
			if ((w_officer_sliders & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
					if ((w_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & w_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID];
					if ((w_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & w_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								if (j == 0 && (w_pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID];
					if ((w_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & w_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								if (j == 0 && (w_pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID];
					if ((w_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & w_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
			}
			
			//Black officers
			long b_officers = bitboard.getFiguresBitboardByPID(Constants.PID_B_BISHOP);
			long b_officer_sliders = b_officers | b_queens; 
			if ((b_officer_sliders & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
					if ((b_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & b_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								if (j == 0 && (b_pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID];
					if ((b_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & b_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID];
					if ((b_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & b_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID];
					if ((b_officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
							if ((officerMove & b_officers) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_OFFICER);
								}
								continue;
							}
							if ((officerMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((officerMove & free) == Figures.NUMBER_0) {
								if (j == 0 && (b_pawns & officerMove) != 0) {
									continue;
								}
								break;
							}
						}
					}
				}
			}
		}
		
		{
			//Castles
			long potentialCastlesPlaces = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
			
			//White officers
			long w_castles = bitboard.getFiguresBitboardByPID(Constants.PID_W_ROOK);
			long w_castle_sliders = w_castles | w_queens;
			if ((w_castle_sliders & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
					if ((w_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & w_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID];
					if ((w_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & w_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID];
					if ((w_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & w_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID];
					if ((w_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & w_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & w_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
			}
			
			//Black officers
			long b_castles = bitboard.getFiguresBitboardByPID(Constants.PID_B_ROOK);
			long b_castle_sliders = b_castles | b_queens;
			if ((b_castle_sliders & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
					if ((b_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & b_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID];
					if ((b_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & b_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID];
					if ((b_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & b_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID];
					if ((b_castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
							if ((castleMove & b_castles) != Figures.NUMBER_0) {
								if (queenAttack) {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								} else {
									states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_CASTLE);
								}
								continue;
							}
							if ((castleMove & b_queens) != Figures.NUMBER_0) {
								states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_QUEEN);
								if (STOP_AT_QUEEN_ATTACK) {
									break;
								}
								queenAttack = true;
								continue;
							}
							if ((castleMove & free) == Figures.NUMBER_0) {
								break;
							}
						}
					}
				}
			}
		}
	}
	
	
	public final int getCost(int figType) {
		return (int) BaseEvalWeights.getFigureMaterialSEE(figType);
	}
}
