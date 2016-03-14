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
package bagaturchess.bitboard.impl.attacks;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class SEE implements ISEE {
	
	private static final boolean DUBUG = false;
	private static final boolean STOP_AT_QUEEN_ATTACK = false;
	private IBitBoard bitboard;
	private int[] myAttacksList;
	private int[] opAttacksList;
	private int myAttacksCount;
	private int opAttacksCount;

	private boolean[] canBeCaptured = new boolean[1];
	
	public SEE(IBitBoard _bitboard) {
		bitboard = _bitboard;
		myAttacksList = new int[12];
		opAttacksList = new int[12];
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
			if (bitboard.getAttacksSupport()) {
				if (bitboard.getFieldsStateSupport()) {
					c_state = bitboard.getFieldsAttacks().getControlArray(colour)[toFieldID];
					oc_state = bitboard.getFieldsAttacks().getControlArray(oppColour)[toFieldID];
				} else {
					c_state = colour == Figures.COLOUR_WHITE ? buildAttacksListByAttacks(Figures.COLOUR_WHITE, toFieldBitboard) : buildAttacksListByAttacks(Figures.COLOUR_BLACK, toFieldBitboard);
					oc_state = oppColour == Figures.COLOUR_WHITE ? buildAttacksListByAttacks(Figures.COLOUR_WHITE, toFieldBitboard) : buildAttacksListByAttacks(Figures.COLOUR_BLACK, toFieldBitboard);
					//c_state = colour == Figures.COLOUR_WHITE ? buildAttacksList(Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
					//oc_state = oppColour == Figures.COLOUR_WHITE ? buildAttacksList(Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
				}
			} else {
				c_state = colour == Figures.COLOUR_WHITE ? buildAttacksList(false, Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(false, Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
				oc_state = oppColour == Figures.COLOUR_WHITE ? buildAttacksList(false, Figures.COLOUR_WHITE, toFieldID, toFieldBitboard) : buildAttacksList(false, Figures.COLOUR_BLACK, toFieldID, toFieldBitboard);
			}
			
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
	
	private int[] buff = new int[3];
	
	public int seeField(int fieldID) {
		
		int figurePID = bitboard.getFigureID(fieldID);
		int figureColour = Figures.getFigureColour(figurePID);
		int figureType = Figures.getFigureType(figurePID);
		
		long fieldBoard = Fields.ALL_A1H1[fieldID];
		
		int w_state = 0;
		int b_state = 0;
		if (bitboard.getAttacksSupport()) {
			if (bitboard.getFieldsStateSupport()) {
				w_state = bitboard.getFieldsAttacks().getControlArray(Figures.COLOUR_WHITE)[fieldID];
				b_state = bitboard.getFieldsAttacks().getControlArray(Figures.COLOUR_BLACK)[fieldID];
			} else {
				w_state = buildAttacksListByAttacks(Figures.COLOUR_WHITE, fieldID);
				b_state = buildAttacksListByAttacks(Figures.COLOUR_BLACK, fieldID);
			}
		} else {
			buff[Figures.COLOUR_WHITE] = 0;
			buff[Figures.COLOUR_BLACK] = 0;
			
			buildAttacksList(fieldID, fieldBoard, buff);
			
			w_state = buff[Figures.COLOUR_WHITE];
			b_state = buff[Figures.COLOUR_BLACK];
		}
		
		int result = -SeeMetadata.getSingleton().seeMove(Figures.TYPE_UNDEFINED, figureType,
				figureColour == Figures.COLOUR_WHITE ? w_state : b_state,
				figureColour == Figures.COLOUR_WHITE ? b_state : w_state);
		
		return result;
	}
	
	public int seeMove(int pieceColour, int pieceType, int toFiledID) {
		
		int w_state = 0;
		int b_state = 0;
		if (bitboard.getAttacksSupport()) {
			if (bitboard.getFieldsStateSupport()) {
				w_state = bitboard.getFieldsAttacks().getControlArray(Figures.COLOUR_WHITE)[toFiledID];
				b_state = bitboard.getFieldsAttacks().getControlArray(Figures.COLOUR_BLACK)[toFiledID];
			} else {
				w_state = buildAttacksListByAttacks(Figures.COLOUR_WHITE, toFiledID);
				b_state = buildAttacksListByAttacks(Figures.COLOUR_BLACK, toFiledID);
			}
		} else {
			buff[Figures.COLOUR_WHITE] = 0;
			buff[Figures.COLOUR_BLACK] = 0;
			
			long fieldBoard = Fields.ALL_A1H1[toFiledID];
			buildAttacksList(toFiledID, fieldBoard, buff);
			
			w_state = buff[Figures.COLOUR_WHITE];
			b_state = buff[Figures.COLOUR_BLACK];
		}
		
		int result = -SeeMetadata.getSingleton().seeMove(pieceType, pieceType,
				pieceColour == Figures.COLOUR_WHITE ? w_state : b_state,
				pieceColour == Figures.COLOUR_WHITE ? b_state : w_state);
		
		return result;
	}
	
	//private int w_state = 0;
	//private int b_state = 0;
	
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
	
	public int buildAttacksList(int colour, int fieldID) {
			
		return buildAttacksList(false, colour, fieldID, Fields.ALL_A1H1[fieldID]);
	}
	
	public int buildAttacksListByAttacks(int colour, long toFieldBitboard) {
		int state = 0;
		
		IPlayerAttacks attacks = bitboard.getPlayerAttacks(colour);
		long all = attacks.allAttacks();
		if ((all & toFieldBitboard) == 0L) {
			return state;
		}
		
		if ((attacks.attacksByType(Figures.TYPE_KING) & toFieldBitboard) != 0L) {
			state = nextState(state, Figures.TYPE_KING, 1);
		}
		
		long pawns = attacks.attacksByType(Figures.TYPE_PAWN);
		if ((pawns & toFieldBitboard) != 0L) {
			long[] unint = attacks.attacksByTypeUnintersected(Figures.TYPE_PAWN);
			int unint_size = attacks.attacksByTypeUnintersectedSize(Figures.TYPE_PAWN);
			if (unint_size == 1) {
				state = nextState(state, Figures.TYPE_PAWN, 1);
			} else {
				for (int i=0; i<unint_size; i++) {
					long cur = unint[i];
					if ((cur & toFieldBitboard) != 0L) {
						state = nextState(state, Figures.TYPE_PAWN, 1);
					} else break;
				}
			}
		}
		
		long knights = attacks.attacksByType(Figures.TYPE_KNIGHT);
		if ((knights & toFieldBitboard) != 0L) {
			long[] unint = attacks.attacksByTypeUnintersected(Figures.TYPE_KNIGHT);
			int unint_size = attacks.attacksByTypeUnintersectedSize(Figures.TYPE_KNIGHT);
			if (unint_size == 1) {
				state = nextState(state, Figures.TYPE_KNIGHT, 1);
			} else {
				for (int i=0; i<unint_size; i++) {
					long cur = unint[i];
					if ((cur & toFieldBitboard) != 0L) {
						state = nextState(state, Figures.TYPE_KNIGHT, 1);
					} else break;
				}
			}
		}
		
		long bishops = attacks.attacksByType(Figures.TYPE_OFFICER);
		if ((bishops & toFieldBitboard) != 0L) {
			long[] unint = attacks.attacksByTypeUnintersected(Figures.TYPE_OFFICER);
			int unint_size = attacks.attacksByTypeUnintersectedSize(Figures.TYPE_OFFICER);
			if (unint_size == 1) {
				state = nextState(state, Figures.TYPE_OFFICER, 1);
			} else {
				for (int i=0; i<unint_size; i++) {
					long cur = unint[i];
					if ((cur & toFieldBitboard) != 0L) {
						state = nextState(state, Figures.TYPE_OFFICER, 1);
					} else break;
				}
			}
		}
		
		long rooks = attacks.attacksByType(Figures.TYPE_CASTLE);
		if ((rooks & toFieldBitboard) != 0L) {
			long[] unint = attacks.attacksByTypeUnintersected(Figures.TYPE_CASTLE);
			int unint_size = attacks.attacksByTypeUnintersectedSize(Figures.TYPE_CASTLE);
			if (unint_size == 1) {
				state = nextState(state, Figures.TYPE_CASTLE, 1);
			} else {
				for (int i=0; i<unint_size; i++) {
					long cur = unint[i];
					if ((cur & toFieldBitboard) != 0L) {
						state = nextState(state, Figures.TYPE_CASTLE, 1);
					} else break;
				}
			}
		}
		
		long queens = attacks.attacksByType(Figures.TYPE_QUEEN);
		if ((queens & toFieldBitboard) != 0L) {
			long[] unint = attacks.attacksByTypeUnintersected(Figures.TYPE_QUEEN);
			int unint_size = attacks.attacksByTypeUnintersectedSize(Figures.TYPE_QUEEN);
			if (unint_size == 1) {
				state = nextState(state, Figures.TYPE_QUEEN, 1);
			} else {
				for (int i=0; i<unint_size; i++) {
					long cur = unint[i];
					if ((cur & toFieldBitboard) != 0L) {
						state = nextState(state, Figures.TYPE_QUEEN, 1);
					} else break;
				}
			}
		}
		
		return state;
	}
	
	public int buildAttacksList(boolean stop, int colour, int toFieldID, long toFieldBitboard) {
		int state = 0;
		
		long free = bitboard.getFreeBitboard();
		
		//Check pawns attacks
		long pawns = 0;
		if (colour == Figures.COLOUR_WHITE) {
			pawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
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
			pawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
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
			long knights = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_KNIGHT);
			if ((knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				state = nextState(state, Figures.TYPE_KNIGHT, Utils.countBits(knights & potentialKnightsPlaces));
			}
		}
		
		if (stop && state > 0) {
			return state;
		}
		
		long queens = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_QUEEN);
		{
			//Officers
			long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
			
			//White officers
			long officers = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER);
			long officer_sliders = officers | queens;
			if ((officer_sliders & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
					if ((officer_sliders & dir & potentialOfficersPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
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
							if ((officerMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
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
							if ((officerMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
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
							if ((officerMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long officerMove = dirMoves[j];
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
							if ((officerMove & free) == Figures.NUMBER_0) {
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
			long castles = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE);
			long castle_sliders = castles | queens;
			if ((castle_sliders & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
				
				{
					long dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
					if ((castle_sliders & dir & potentialCastlesPlaces) != Figures.NUMBER_0 ) {
						boolean queenAttack = false;
						long[] dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
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
							if ((castleMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
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
							if ((castleMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
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
							if ((castleMove & free) == Figures.NUMBER_0) {
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
						for (int j=0; j<dirMoves.length; j++) {
							long castleMove = dirMoves[j];
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
							if ((castleMove & free) == Figures.NUMBER_0) {
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
			long king = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_KING);
			if ((king & potentialKingPlaces) != Figures.NUMBER_0) {
				state = nextState(state, Figures.TYPE_KING);
			}
		}
		
		return state;
	}
	
	private void buildAttacksList(int toFieldID, long toFieldBitboard, int[] states) {
		
		//long w_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
		//long b_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);
		long free = bitboard.getFreeBitboard();
		
		//Check knights
		long potentialKnightsPlaces = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
		{
			long w_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT);
			if ((w_knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_KNIGHT, Utils.countBits(w_knights & potentialKnightsPlaces));
			}
			long b_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT);
			if ((b_knights & potentialKnightsPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_KNIGHT, Utils.countBits(b_knights & potentialKnightsPlaces));
			}
		}
		
		{
			//Check opponent king
			long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
			long w_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING);
			if ((w_king & potentialKingPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_WHITE] = nextState(states[Figures.COLOUR_WHITE], Figures.TYPE_KING);
			}
			long b_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING);
			if ((b_king & potentialKingPlaces) != Figures.NUMBER_0) {
				states[Figures.COLOUR_BLACK] = nextState(states[Figures.COLOUR_BLACK], Figures.TYPE_KING);
			}
		}
		
		//Check pawns attacks
		long w_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
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
		long b_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
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
		
		long w_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		long b_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		
		{
			//Officers
			long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
			
			//White officers
			long w_officers = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
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
			long b_officers = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
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
			long w_castles = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
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
			long b_castles = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
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
	
	/**
	 * skipOneAttackOfType the type starting sequence
	 * 		Attention!!! : If the sequence is started from pawn, pass the pawn's type only when the sequence is a capture sequence initially,
	 * 		otherwise Figures.TYPE_UNDEFINED.
	 * 
	 * TODO: Add queen in pawn promotion captures
	 */
	private int buildAttacksList(long toFieldBitboard, IPlayerAttacks attacks, int[] attacksList, int startIndex, int skipOneAttackOfType) {
		int count = startIndex;
		
		/**
		 * Has at least one attack on that field
		 */
		if ((attacks.allAttacks() & toFieldBitboard) != 0L) {
			
			/**
			 * Start with pawns
			 */
			int curType = Figures.TYPE_PAWN;
			while (curType != Figures.TYPE_MAX) {
				
				long curTypeAttacks = attacks.attacksByType(curType);
				
				/**
				 * Has at least one type attack on that field
				 */
				if ((curTypeAttacks & toFieldBitboard) != 0L) {
					
					/**
					 * Count type attacks
					 */
					int size = attacks.attacksByTypeUnintersectedSize(curType);
					long[] bitboard = attacks.attacksByTypeUnintersected(curType);
					for (int i=0; i<size; i++) {
						if ((bitboard[i] & toFieldBitboard) != 0L) {
							/**
							 * Skip one type attack
							 */
							if (skipOneAttackOfType == curType) {
								skipOneAttackOfType = Figures.TYPE_UNDEFINED;
							} else {
								attacksList[count] = getCost(curType);
								count++;
							}
						}
					}
				}
				
				/**
				 * Next piece type
				 */
				curType = Figures.nextType(curType);
			}
			
		}
		
		return count;
	}
	
	public final int getCost(int figType) {
		//return bitboard.getBaseEvaluation().getMaterial(figType);
		return (int) BaseEvalWeights.getFigureMaterialSEE(figType);
	}
}
