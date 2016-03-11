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
package bagaturchess.bitboard.impl.attacks.control;


import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.StaticScores;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateMachine;


public class FieldsStateMachine implements IFieldsAttacks {
	
	private static final boolean SUPPORT_TOTAL_STATES = false;
	private static final boolean TRACE_TRANSISIONS = false;
	
	private int[][][] machine = FieldAttacksStateMachine.getInstance().getMachine();
	private FieldAttacks[] states = FieldAttacksStateMachine.getInstance().getAllStatesList();
	
	int KS_TABLE [] = {  0,  2,  3,  6, 12, 18, 25, 37, 50, 75,
      100,125,150,175,200,225,250,275,300,325,
      350,375,400,425,450,475,500,525,550,575, 
      600,600,600,600,600,600,600,600,600,600};
  
  	int KS_TABLE_FLAGS [] = {
  	  //      . P N N R R R R Q Q Q Q Q Q Q Q K K K K K K K K K K K K K K K K
  	  //            P   P N N   P N N R R R R   P N N R R R R Q Q Q Q Q Q Q Q
  	  //                    P       P   N N N       P   P N N   P N N R R R R
  	          0,0,0,0,0,0,1,1,0,1,2,2,2,3,3,3,0,0,0,0,1,1,2,2,2,3,3,3,3,3,3,3 };
  
	//private int[][] w_attacksMatrix;
	//private int[][] b_attacksMatrix;
	
	private int[][] controlPointsByColourAndField;
	
	private long[] controlBitboards;
	private long[][] hangingBitboards;
	
	private Board bitboard;
	//private int[] hungedPieces = new int[2];
	
	FieldStateMachine totalMachine;
	private int[] totalFieldsStates;
	//private int total_neutralScores = 0;
	private int total_whiteScores = 0;
	private int total_blackScores = 0;
	private int[][] scores = new int[2][7];
	
	private IAttackListener attackListner;
	
	
	public FieldsStateMachine(Board _bitboard, IAttackListener _attackListner) {
		bitboard = _bitboard;
		attackListner = _attackListner;
		//w_attacksMatrix = new int[8][8];
		//b_attacksMatrix = new int[8][8];
		controlPointsByColourAndField = new int[Figures.COLOUR_MAX][Fields.ID_MAX];
		controlBitboards = new long[Figures.COLOUR_MAX];
		hangingBitboards = new long[Figures.COLOUR_MAX][Figures.TYPE_MAX];
		
		if (SUPPORT_TOTAL_STATES) {
			totalMachine = FieldStateMachine.getInstance();
			totalFieldsStates = new int[Figures.ID_MAX];
		}
	}
	
	public void removeAttack(int colour, int type, int fieldID, long fieldBitboard) {
		
		if (attackListner != null) attackListner.removeAttack(colour, type, fieldID, fieldBitboard);
		
		//int letter = Fields.LETTERS[fieldID];
		//int digit = Fields.DIGITS[fieldID];
		
		int opColour = Figures.OPPONENT_COLOUR[colour];
		
		int[] alist = controlPointsByColourAndField[colour];
		int[] oalist = controlPointsByColourAndField[opColour];
		
		int currentState = alist[fieldID]; 
		int nextState = machine[IFieldsAttacks.OP_REM_ATTACK][type][currentState];
		if (nextState == -1) {
			String msg = "Current state: " +  states[currentState] + ", type " + Figures.TYPES_SIGN[type];
			throw new IllegalStateException(msg);
			//System.out.println(msg);
			//System.exit(-1);
		}
		alist[fieldID] = nextState;
		
		/**
		 * Control
		 */
		int opState = oalist[fieldID];
		if (nextState < opState) {
			controlBitboards[colour] &= ~fieldBitboard;
			controlBitboards[opColour] |= fieldBitboard;
		} else if (nextState > opState) {
			controlBitboards[colour] |= fieldBitboard;
			controlBitboards[opColour] &= ~fieldBitboard;
		} else {
			controlBitboards[colour] &= ~fieldBitboard;
			controlBitboards[opColour] &= ~fieldBitboard;
		}
		
		if (SUPPORT_TOTAL_STATES) {
			int t_currentState = totalFieldsStates[fieldID];
			int t_nextState = totalMachine.nextState(colour, type, FieldStateMachine.TRANSITION_REM_ATTACK, t_currentState);
			if (t_nextState == -1) {
				throw new IllegalStateException(transToString(t_currentState, FieldStateMachine.TRANSITION_REM_ATTACK));
			}
			totalFieldsStates[fieldID] = t_nextState;
			
			updateTotalMachine(fieldID, t_currentState, t_nextState);
		}
	}
	
	public void addAttack(int colour, int type, int fieldID, long fieldBitboard) {
		
		if (attackListner != null) attackListner.addAttack(colour, type, fieldID, fieldBitboard);
		
		//int letter = Fields.LETTERS[fieldID];
		//int digit = Fields.DIGITS[fieldID];
		
		int opColour = Figures.OPPONENT_COLOUR[colour];
		
		int[] alist = controlPointsByColourAndField[colour];
		int[] oalist = controlPointsByColourAndField[opColour];
		
		int currentState = alist[fieldID];
		//if (currentBits == -1) return;
		int nextState = machine[IFieldsAttacks.OP_ADD_ATTACK][type][currentState];
		if (nextState == -1) {
			String msg = "Current state: " + states[currentState] + ", type " + Figures.TYPES_SIGN[type];
			throw new IllegalStateException(msg);
			//System.out.println(msg);
			//System.exit(-1);
		}
		alist[fieldID] = nextState;
		
		/**
		 * Control
		 */
		int opState = oalist[fieldID];
		if (nextState > opState) {
			controlBitboards[colour] |= fieldBitboard;
			controlBitboards[opColour] &= ~fieldBitboard;
		} else if (nextState < opState) {
			controlBitboards[colour] &= ~fieldBitboard;
			controlBitboards[opColour] |= fieldBitboard;
		} else {
			controlBitboards[colour] &= ~fieldBitboard;
			controlBitboards[opColour] &= ~fieldBitboard;
		}
		
		if (SUPPORT_TOTAL_STATES) {
			int t_currentState = totalFieldsStates[fieldID];
			int t_nextState = totalMachine.nextState(colour, type, FieldStateMachine.TRANSITION_ADD_ATTACK, t_currentState);
			if (t_nextState == -1) {
				throw new IllegalStateException(transToString(t_currentState, FieldStateMachine.TRANSITION_REM_ATTACK));
			}
			totalFieldsStates[fieldID] = t_nextState;
			
			updateTotalMachine(fieldID, t_currentState, t_nextState);
		}
	}
	
	public void removeFigure(int colour, int type, int fieldID) {
		if (SUPPORT_TOTAL_STATES) {
			int currentState = totalFieldsStates[fieldID];
			int t_nextState = totalMachine.nextState(colour, type, FieldStateMachine.TRANSITION_REM_FIGURE, currentState);
			if (t_nextState == -1) {
				throw new IllegalStateException(transToString(currentState, FieldStateMachine.TRANSITION_REM_ATTACK));
			}
			totalFieldsStates[fieldID] = t_nextState;
			
			updateTotalMachine(fieldID, currentState, t_nextState);
		}
	}
	
	public void addFigure(int colour, int type, int fieldID) {
		if (SUPPORT_TOTAL_STATES) {
			int currentState = totalFieldsStates[fieldID];
			int t_nextState = totalMachine.nextState(colour, type, FieldStateMachine.TRANSITION_ADD_FIGURE, currentState);
			if (t_nextState == -1) {
				throw new IllegalStateException(transToString(currentState, FieldStateMachine.TRANSITION_REM_ATTACK));
			}
			totalFieldsStates[fieldID] = t_nextState;
			
			updateTotalMachine(fieldID, currentState, t_nextState);
		}
	}
	
	private void updateTotalMachine(int fieldID, int oldState, int newState) {

		/*updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_MATERIAL, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_MATERIAL, oldState, newState);

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_MATERIAL_WIN, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_MATERIAL_WIN, oldState, newState);

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_MATERIAL_LOSS, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_MATERIAL_LOSS, oldState, newState);

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_CONTROL, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_CONTROL, oldState, newState);

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_ATTACK, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_ATTACK, oldState, newState);*/

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_ALL, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_ALL, oldState, newState);

		updateTotalMachineSlot(Figures.COLOUR_BLACK, StaticScores.SLOT_MOBILITY, oldState, newState);
		updateTotalMachineSlot(Figures.COLOUR_WHITE, StaticScores.SLOT_MOBILITY, oldState, newState);
		
		//StaticScores.SLOT_ATTACK
		/*int dec = totalMachine.getScores().getScore_VPNeutral(oldState);
		int inc = totalMachine.getScores().getScore_VPNeutral(newState);
		
		//total_neutralScores -= dec;
		//total_neutralScores += inc;
		
		dec = totalMachine.getScores().getScore_VPWhite(oldState);
		inc = totalMachine.getScores().getScore_VPWhite(newState);
		total_whiteScores -= dec;
		total_whiteScores += inc;

		dec = totalMachine.getScores().getScore_VPBlack(oldState);
		inc = totalMachine.getScores().getScore_VPBlack(newState);
		total_blackScores -= dec;
		total_blackScores += inc;*/

		if (TRACE_TRANSISIONS) {
			/*String fieldSign = Fields.getFieldSign_UC(fieldID);
			FieldState old = totalMachine.getStates().get(oldState);
			FieldState _new = totalMachine.getStates().get(newState);
			
			System.out.println(fieldSign + " TRANS " + old + " -> " + _new + ", value " + (inc-dec));*/
		}
	}
	
	private void updateTotalMachineSlot(int colour, int slot, int oldState, int newState) {
		scores[colour][slot] -= totalMachine.getScores().getScore(colour, slot, oldState);
		scores[colour][slot] += totalMachine.getScores().getScore(colour, slot, newState);
		//int opColour = Figures.OPPONENT_COLOUR[colour];
		//scores[opColour][slot] -= totalMachine.getScores().getScore(opColour, slot, oldState);
		//scores[opColour][slot] += totalMachine.getScores().getScore(opColour, slot, newState);

	}
	
	public int getScore_BeforeMove(int colour) {
		//if (colour == Figures.COLOUR_WHITE) {
			return scores[colour][StaticScores.SLOT_ALL];
			//return total_whiteScores;
		//} else {
			//return scores[Figures.COLOUR_BLACK][StaticScores.SLOT_ALL];
			//return total_blackScores;
		//}
	}

	public int getScore_AfterMove(int colour) {
		//if (colour == Figures.COLOUR_WHITE) {
		int opColour = Figures.OPPONENT_COLOUR[colour];
			return -scores[opColour][StaticScores.SLOT_ALL];
			//return -total_blackScores;
		//} else {
			//return -scores[Figures.COLOUR_WHITE][StaticScores.SLOT_ALL];
			//return -total_whiteScores;
		//}
	}

	public int getScore_ForEval(int colour) {
		//if (true) throw new IllegalStateException();
		
		if (colour == Figures.COLOUR_WHITE) {
			return scores[Figures.COLOUR_WHITE][StaticScores.SLOT_MOBILITY];
		} else {
			return 0;//scores[Figures.COLOUR_BLACK][StaticScores.SLOT_MOBILITY];
		}
	}
	
	
	public void clearScore() {
		throw new IllegalStateException();
		//total_whiteScores = 0;
	}
	
	public String transToString(int state, int op) {
		String result = FieldStateMachine.TRANSITION_SIGN[op];
		result += " | " + totalMachine.stateToString(state);
		return result;
	}
	
	public int[] getControlArray(int colour) {
		return controlPointsByColourAndField[colour];
	}
	
	public long getControlBitboard(int colour) {
		return controlBitboards[colour];
	}
	
	public long getPotentiallyHangingPieces(int colour, int type) {
		if (true) throw new IllegalStateException();
		return hangingBitboards[colour][type];
	}
  
	/*public long w_KingSafety() {
		int w_king_field_id = bitboard.getFieldID(Figures.WHITE_KING_ID);
		int[] w_ffront = KingSurrounding.W_FFRONT[w_king_field_id];
		int[] w_front = KingSurrounding.W_FRONT[w_king_field_id];
		int[] w_back = KingSurrounding.W_BACK[w_king_field_id];
		
		int counter = 0;
		int pattern = 0;
		
		int[] w_states = controlPointsByColourAndField[Figures.COLOUR_WHITE];
		int[] b_states = controlPointsByColourAndField[Figures.COLOUR_BLACK];

		for (int i=0; i<w_ffront.length; i++) {
			int fieldID = w_ffront[i];
			if (b_states[fieldID] > 0) {
				pattern |= states[b_states[fieldID]].getPattern();
			}
		}
		
		for (int i=0; i<w_back.length; i++) {
			int fieldID = w_back[i];
			if (b_states[fieldID] > 0) {
				counter++;
				pattern |= states[b_states[fieldID]].getPattern();
				
				int state = w_states[fieldID];
				if (!states[state].hasNonKingAttack()) {
					counter++;
				}
			}
		}
		
		for (int i=0; i<w_front.length; i++) {
			int fieldID = w_front[i];
			if (b_states[fieldID] > 0) {
				counter++;
				pattern |= states[b_states[fieldID]].getPattern();
				
				int state = w_states[fieldID];
				if (!states[state].hasNonKingAttack()) {
					counter++;
				}
				
				int figID = bitboard.getFigureID(fieldID);
				if (figID == Figures.DUMMY_FIGURE_ID) {
					counter++;
				} else {
					if (Figures.FIGURES_COLOURS[figID] != Figures.COLOUR_WHITE) {
						counter++;
					}
				}
			}
		}
		
		counter += KS_TABLE_FLAGS[pattern];
		
		int eval = -KS_TABLE[counter];
		
		return eval;
	}
	
	public long b_KingSafety() {
		int b_king_field_id = bitboard.getFieldID(Figures.BLACK_KING_ID);
		int[] b_ffront = KingSurrounding.B_FFRONT[b_king_field_id];
		int[] b_front = KingSurrounding.B_FRONT[b_king_field_id];
		int[] b_back = KingSurrounding.B_BACK[b_king_field_id];
		
		int counter = 0;
		int pattern = 0;
		int[] w_states = controlPointsByColourAndField[Figures.COLOUR_WHITE];
		int[] b_states = controlPointsByColourAndField[Figures.COLOUR_BLACK];
		
		for (int i=0; i<b_ffront.length; i++) {
			int fieldID = b_ffront[i];
			if (w_states[fieldID] > 0) {
				pattern |= states[w_states[fieldID]].getPattern();
			}
		}
		
		for (int i=0; i<b_back.length; i++) {
			int fieldID = b_back[i];
			if (w_states[fieldID] > 0) {
				counter++;
				pattern |= states[w_states[fieldID]].getPattern();
				
				int state = b_states[fieldID];
				if (!states[state].hasNonKingAttack()) {
					counter++;
				}
			}
		}
		
		for (int i=0; i<b_front.length; i++) {
			int fieldID = b_front[i];
			if (w_states[fieldID] > 0) {
				counter++;
				pattern |= states[w_states[fieldID]].getPattern();
				
				int state = b_states[fieldID];
				if (!states[state].hasNonKingAttack()) {
					counter++;
				}
				
				int figID = bitboard.getFigureID(fieldID);
				if (figID == Figures.DUMMY_FIGURE_ID) {
					counter++;
				} else {
					if (Figures.FIGURES_COLOURS[figID] != Figures.COLOUR_BLACK) {
						counter++;
					}
				}
			}
		}
		
		counter += KS_TABLE_FLAGS[pattern];
		
		int eval = -KS_TABLE[counter];
		
		return eval;
	}*/
	
	/*public long getKingSafety(int colour) {
		if (colour == Figures.COLOUR_WHITE) {
			return w_KingSafety() - b_KingSafety();
		} else {
			return b_KingSafety() - w_KingSafety();
		}
	}*/
	
	public void checkConsistency() {
		
		IPlayerAttacks whiteAttacks = bitboard.getPlayerAttacks(Figures.COLOUR_WHITE);
		IPlayerAttacks blackAttacks = bitboard.getPlayerAttacks(Figures.COLOUR_BLACK);

		for (int fieldID=0; fieldID<Fields.ALL_A1H1.length; fieldID++) {
			/*if (fieldID == 0 || fieldID == 17 || fieldID == 34 ) {
				continue;
			}*/
			
			long field = Fields.ALL_A1H1[fieldID];
			
			int whiteState = controlPointsByColourAndField[Figures.COLOUR_WHITE][fieldID];
			int blackState = controlPointsByColourAndField[Figures.COLOUR_BLACK][fieldID];
			
			FieldAttacks whiteObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteState];
			FieldAttacks blackObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[blackState];
			
			check(whiteObj, whiteAttacks, Figures.TYPE_PAWN, field);
			if (IFieldsAttacks.MINOR_UNION) {
				checkMinors(whiteObj, whiteAttacks, field);
			} else {
				check(whiteObj, whiteAttacks, Figures.TYPE_KNIGHT, field);
				check(whiteObj, whiteAttacks, Figures.TYPE_OFFICER, field);
			}
			check(whiteObj, whiteAttacks, Figures.TYPE_CASTLE, field);
			check(whiteObj, whiteAttacks, Figures.TYPE_QUEEN, field);
			check(whiteObj, whiteAttacks, Figures.TYPE_KING, field);
			
			check(blackObj, blackAttacks, Figures.TYPE_PAWN, field);
			if (IFieldsAttacks.MINOR_UNION) {
				checkMinors(blackObj, blackAttacks, field);
			} else {
				check(blackObj, blackAttacks, Figures.TYPE_KNIGHT, field);
				check(blackObj, blackAttacks, Figures.TYPE_OFFICER, field);
			}
			check(blackObj, blackAttacks, Figures.TYPE_CASTLE, field);
			check(blackObj, blackAttacks, Figures.TYPE_QUEEN, field);
			check(blackObj, blackAttacks, Figures.TYPE_KING, field);
			
			if (SUPPORT_TOTAL_STATES) {
				int stateID = totalFieldsStates[fieldID];
				FieldState stateObj = totalMachine.getStates().get(stateID);
				if (stateObj.getWhiteAttacks().getId() != controlPointsByColourAndField[Figures.COLOUR_WHITE][fieldID]) {
					throw new IllegalStateException("ts=" + stateObj.getWhiteAttacks().getId() + ", other=" + controlPointsByColourAndField[Figures.COLOUR_WHITE][fieldID]);
				}
				if (stateObj.getBlackAttacks().getId() != controlPointsByColourAndField[Figures.COLOUR_BLACK][fieldID]) {
					throw new IllegalStateException("ts=" + stateObj.getBlackAttacks().getId() + ", other=" + controlPointsByColourAndField[Figures.COLOUR_BLACK][fieldID]);
				}
				
				int figureID = bitboard.getFigureID(fieldID);
				if (figureID == Constants.PID_NONE) {
					if (stateObj.getFigureOnFieldColour() != Figures.COLOUR_UNSPECIFIED) {
						throw new IllegalStateException("ts=" + stateObj.getFigureOnFieldColour() + ", other=" + Constants.PID_NONE);
					}
				} else {
					int figureColour = Figures.getFigureColour(figureID);
					int figureType = Figures.getFigureType(figureID);
					if (stateObj.getFigureOnFieldColour() != figureColour) {
						throw new IllegalStateException("ts=" + stateObj.getFigureOnFieldColour() + ", other=" + figureColour);
					}
					if (stateObj.getFigureOnFieldType() != figureType) {
						throw new IllegalStateException("ts=" + stateObj.getFigureOnFieldType() + ", other=" + figureType);
					}					
				}
			}
		}
	}
	
	private void check(FieldAttacks state, IPlayerAttacks playerAttacks, int type, long field) {
		
		int fromA_count = playerAttacks.countAttacks(type, field);
		
		int fromF_count = -1;
		switch (type) {
			case Figures.TYPE_PAWN:
				fromF_count = state.paCount();
				break;
			case Figures.TYPE_KNIGHT:
				fromF_count = state.knaCount();
				break;
			case Figures.TYPE_OFFICER:
				fromF_count = state.oaCount();
				break;
			case Figures.TYPE_CASTLE:
				fromF_count = state.raCount();
				break;
			case Figures.TYPE_QUEEN:
				fromF_count = state.qaCount();
				break;
			case Figures.TYPE_KING:
				fromF_count = state.kaCount();
				break;
			default:
				throw new IllegalStateException();
		}
		
		if (fromA_count !=  fromF_count) {
			throw new IllegalStateException(Figures.TYPES_SIGN[type] + ": fromA_count=" + fromA_count + ", fromF_count=" + fromF_count);
		}
	}
	
	private void checkMinors(FieldAttacks state, IPlayerAttacks playerAttacks, long field) {
		int fromA_count = playerAttacks.countAttacks(Figures.TYPE_OFFICER, field) + playerAttacks.countAttacks(Figures.TYPE_KNIGHT, field);
		int fromF_count = state.maCount(); 
		if (fromA_count !=  fromF_count) {
			throw new IllegalStateException("Minors" + ": fromA_count=" + fromA_count + ", fromF_count=" + fromF_count);
		}
	}
}
