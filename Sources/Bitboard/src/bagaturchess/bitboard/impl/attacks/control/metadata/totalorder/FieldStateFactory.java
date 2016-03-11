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
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;

public class FieldStateFactory {
	
	private static FieldAttacksStateMachine attacksMachine = FieldAttacksStateMachine.getInstance();
	
	//public static FieldAttacks create(int pa, int kna, int oa, int ma, int ra, int qa, int ka, int xa);
	
	public static FieldState modify(int operation, int figureColour, int figureType, FieldState source) {
		
		FieldState result = source.clone();
		
		switch(operation) {
			case FieldStateMachine.TRANSITION_ADD_ATTACK:
				modify_addAttack(result, figureColour, figureType);
				break;
			case FieldStateMachine.TRANSITION_REM_ATTACK:
				modify_remAttack(result, figureColour, figureType);
				break;
			case FieldStateMachine.TRANSITION_ADD_FIGURE:
				modify_addFigure(result, figureColour, figureType);
				break;
			case FieldStateMachine.TRANSITION_REM_FIGURE:
				modify_remFigure(result, figureColour, figureType);
				break;
			default:
				throw new IllegalStateException();
		}
		
		return result;
	}
	
	public static FieldState modify_addAttack(FieldState result, int figureColour, int figureType) {
		if (figureColour == Figures.COLOUR_WHITE) {
			int currentWhiteAttackID = result.whiteAttacks.getId();
			int nextWhiteAttackID = attacksMachine.getMachine()[IFieldsAttacks.OP_ADD_ATTACK][figureType][currentWhiteAttackID];
			if (nextWhiteAttackID == -1) {
				result = null;
			} else {
				FieldAttacks nextAttacksObj = attacksMachine.getAllStatesList()[nextWhiteAttackID];
				result.whiteAttacks = nextAttacksObj;
			}
		} else if (figureColour == Figures.COLOUR_BLACK) {
				int currentBlackAttackID = result.blackAttacks.getId();
				int nextBlackAttackID = attacksMachine.getMachine()[IFieldsAttacks.OP_ADD_ATTACK][figureType][currentBlackAttackID];
				if (nextBlackAttackID == -1) {
					result = null;
				} else {
					FieldAttacks nextAttacksObj = attacksMachine.getAllStatesList()[nextBlackAttackID];
					result.blackAttacks = nextAttacksObj;
				}
		} else {
			throw new IllegalStateException();
		}
		
		return result;
	}
	
	public static FieldState modify_remAttack(FieldState result, int figureColour, int figureType) {
		if (figureColour == Figures.COLOUR_WHITE) {
			int currentWhiteAttackID = result.whiteAttacks.getId();
			int nextWhiteAttackID = attacksMachine.getMachine()[IFieldsAttacks.OP_REM_ATTACK][figureType][currentWhiteAttackID];
			if (nextWhiteAttackID == -1) {
				result = null;
			} else {
				FieldAttacks nextAttacksObj = attacksMachine.getAllStatesList()[nextWhiteAttackID];
				result.whiteAttacks = nextAttacksObj;
			}
		} else if (figureColour == Figures.COLOUR_BLACK) {
				int currentBlackAttackID = result.blackAttacks.getId();
				int nextBlackAttackID = attacksMachine.getMachine()[IFieldsAttacks.OP_REM_ATTACK][figureType][currentBlackAttackID];
				if (nextBlackAttackID == -1) {
					result = null;
				} else {
					FieldAttacks nextAttacksObj = attacksMachine.getAllStatesList()[nextBlackAttackID];
					result.blackAttacks = nextAttacksObj;
				}
		} else {
			throw new IllegalStateException();
		}
		
		return result;
	}
	
	public static FieldState modify_addFigure(FieldState result, int figureColour, int figureType) {
		int opColour = Figures.OPPONENT_COLOUR[figureColour];
		if (result.figureOnFieldColour == Figures.COLOUR_UNSPECIFIED // empty field
				|| result.figureOnFieldColour == opColour //Opponent piece on field
			) {
			result.figureOnFieldColour = figureColour;
			result.figureOnFieldType = figureType;
		} else {
			result = null;
		}
		
		return result;
	}
	
	public static FieldState modify_remFigure(FieldState result, int figureColour, int figureType) {
		if (result.figureOnFieldColour != Figures.COLOUR_UNSPECIFIED // has piece
				&& result.figureOnFieldColour == figureColour //Opponent piece on field
			) {
			result.figureOnFieldColour = Figures.COLOUR_UNSPECIFIED;
			result.figureOnFieldType = Figures.TYPE_UNDEFINED;
		} else {
			result = null;
		}
		
		return result;
	}
}
