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
package bagaturchess.bitboard.impl.attacks.control.metadata;

import java.util.List;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;


public class StaticScores {
	
	public static final int VIEW_POINT_WHITE = 0;
	public static final int VIEW_POINT_BLACK = 1;

	public static final int SLOT_MATERIAL = 0;
	public static final int SLOT_MATERIAL_WIN = 1;
	public static final int SLOT_MATERIAL_LOSS = 2;
	public static final int SLOT_CONTROL = 3;
	public static final int SLOT_ATTACK = 4;
	public static final int SLOT_ALL = 5;
	public static final int SLOT_MOBILITY = 6;
	
	private static final int BONUS_ATTACK_QUEEN = 1;
	private static final int BONUS_ATTACK_ROOK = 2;
	private static final int BONUS_ATTACK_MINOR = 3;

	private static final int BONUS_CONTROL_QUEEN = 1;
	private static final int BONUS_CONTROL_ROOK = 3;
	private static final int BONUS_CONTROL_MINOR = 5;

	private FieldStateMachine machine;
	private List<FieldState> states; 
	private int[][][] scores;
	//private int[] b_scores;
	
	public StaticScores(FieldStateMachine _machine) {
		
		machine = _machine;
		states = machine.getStates();
		scores = new int[3][7][states.size()];
		//b_scores = new int[states.size()];
		
		System.out.print("Start static scores calculation ... ");
		
		long start = System.currentTimeMillis();
		init();
		long end = System.currentTimeMillis();
		
		System.out.println("Ready! Time: " + (end - start) + " ms");
	}

	public int getScore(int colour, int slot, int stateID) {
		if (colour == Figures.COLOUR_WHITE) {
			return scores[VIEW_POINT_WHITE][slot][stateID];
		} else {
			return scores[VIEW_POINT_BLACK][slot][stateID];
		}
	}
	
	private void addScores(int viewpoint, int slot, int stateID, int _scores) {
		scores[viewpoint][slot][stateID] += _scores;
		scores[viewpoint][SLOT_ALL][stateID] += _scores;
	}
	
	private void init() {
		List<FieldState> states = machine.getStates();
		
		for (int i=0; i<states.size(); i++) {
			
			FieldState currentState = states.get(i);
			
			int figColour = currentState.getFigureOnFieldColour();
			int figType = currentState.getFigureOnFieldType();
			FieldAttacks whiteA = currentState.getWhiteAttacks();
			FieldAttacks blackA = currentState.getBlackAttacks();
			
			if (figColour != Figures.COLOUR_UNSPECIFIED) {
				/**
				 * Figure on field
				 */
				
				//Material
				int materialScores = calcMaterialScores(figType);
				if (figColour == Figures.COLOUR_WHITE) {
					addScores(VIEW_POINT_WHITE, SLOT_MATERIAL, i, materialScores);
					addScores(VIEW_POINT_BLACK, SLOT_MATERIAL, i, -materialScores);
				} else {
					addScores(VIEW_POINT_BLACK, SLOT_MATERIAL, i, materialScores);
					addScores(VIEW_POINT_WHITE, SLOT_MATERIAL, i, -materialScores);
				}
				
				//See
				if (figColour == Figures.COLOUR_WHITE) {
					int see_win = -calcSEEScores(figType, whiteA, blackA);
					if (see_win > 0) { // black wins
						addScores(VIEW_POINT_BLACK, SLOT_MATERIAL_WIN, i, see_win);
						addScores(VIEW_POINT_WHITE, SLOT_MATERIAL_LOSS, i, -40);
					} else {
						int blocked = calcBlockedPiecesScores(figType, whiteA, blackA);
						/*if (-blocked <= 0) {
							System.out.println("blocked=" + blocked);
						}*/
						//addScores(VIEW_POINT_BLACK, SLOT_MATERIAL_WIN, i, blocked);
						addScores(VIEW_POINT_WHITE, SLOT_MATERIAL_LOSS, i, -blocked);
					}
				} else {
					int see_win = -calcSEEScores(figType, blackA, whiteA);
					if (see_win > 0) { // white wins
						addScores(VIEW_POINT_WHITE, SLOT_MATERIAL_WIN, i, see_win);
						addScores(VIEW_POINT_BLACK, SLOT_MATERIAL_LOSS, i, -40);
					} else {
						int blocked = calcBlockedPiecesScores(figType, blackA, whiteA);
						/*if (-blocked <= 0) {
							System.out.println("blocked=" + blocked);
						}*/
						addScores(VIEW_POINT_BLACK, SLOT_MATERIAL_LOSS, i, -blocked);
						//addScores(VIEW_POINT_WHITE, SLOT_MATERIAL_LOSS, i, blocked);
					}
				}
				
				
				//addScores(i, calcBlockedPiecesScores(figColour, figType, whiteA, blackA));
			} else {
				
				//Control
				int w_safeAttacks = calcSafeAttacksScores(whiteA, blackA);
				int b_safeAttacks = calcSafeAttacksScores(blackA, whiteA);
				
				addScores(VIEW_POINT_WHITE, SLOT_MOBILITY, i, w_safeAttacks);
				addScores(VIEW_POINT_WHITE, SLOT_MOBILITY, i, -b_safeAttacks);
				
				addScores(VIEW_POINT_BLACK, SLOT_MOBILITY, i, b_safeAttacks);
				addScores(VIEW_POINT_BLACK, SLOT_MOBILITY, i, -w_safeAttacks);
				
				//Attacks
				int w_nonSafeAttacks = calcAttacksScores(whiteA);
				int b_nonSafeAttacks = calcAttacksScores(blackA);
				
				addScores(VIEW_POINT_WHITE, SLOT_MOBILITY, i, w_nonSafeAttacks);
				addScores(VIEW_POINT_WHITE, SLOT_MOBILITY, i,	-b_nonSafeAttacks);
				
				addScores(VIEW_POINT_BLACK, SLOT_MOBILITY, i, b_nonSafeAttacks);
				addScores(VIEW_POINT_BLACK, SLOT_MOBILITY, i, -w_nonSafeAttacks);
				
				/*(if (w_safeAttacks > 0) {
					System.out.println("w_safeAttacks=" + w_safeAttacks);
				}
				if (b_safeAttacks > 0) {
					System.out.println("b_safeAttacks=" + b_safeAttacks);
				}
				
				if (w_nonSafeAttacks > 0) {
					System.out.println("w_nonSafeAttacks=" + w_nonSafeAttacks);
				}
				if (b_nonSafeAttacks > 0) {
					System.out.println("b_nonSafeAttacks=" + b_nonSafeAttacks);
				}*/
			}
			
			/*if (w_scores[i] > 500 || w_scores[i] < -500) {
				System.out.println(currentState + "->" + w_scores[i]);
			}*/
		}
	}
	
	private int calcMaterialScores(int figType) {
		
		switch(figType) {
			case Figures.TYPE_KING:
				return 225; //TODO: Extract constant globally
			default:
				return BaseEvalWeights.getFigureCost(figType);
		}
	}

	private int calcSEEScores(int figType, FieldAttacks attacked, FieldAttacks toPlay) {
		
		int result = 0;
		
		//if (figColour == Figures.COLOUR_WHITE) {
			//black can capture
			result = -SeeMetadata.FIELD_SEE[figType][attacked.getId()][toPlay.getId()];
		//} else {
			//white can capture
			//result = -(-SeeMetadata.FIELD_SEE[figType][blackA.getId()][whiteA.getId()]);
		//}
		
		return result;
	}

	private int calcBlockedPiecesScores(int figType, FieldAttacks attacked, FieldAttacks toPlay) {
		int result = 0;
		
		FieldAttacks curAttacked_backup = null;
		FieldAttacks curAttacked = attacked;
		
		int points;
		while ((points = -calcSEEScores(figType, curAttacked, toPlay)) <= 0) {
			int typeToRemove = curAttacked.getMaxType();
			if (typeToRemove == Figures.TYPE_UNDEFINED) {
				return 0;
			}
			int curAttackedState = curAttacked.getId();
			curAttackedState = FieldAttacksStateMachine.getInstance().getMachine()[IFieldsAttacks.OP_REM_ATTACK][typeToRemove][curAttackedState];
			curAttacked_backup = curAttacked;
			curAttacked = FieldAttacksStateMachine.getInstance().getAllStatesList()[curAttackedState];
		}
		
		if (curAttacked_backup != null) {
			result += 10 * BONUS_ATTACK_QUEEN * curAttacked_backup.qaCount();
			result += 7 * BONUS_ATTACK_ROOK * curAttacked_backup.raCount();
			if (IFieldsAttacks.MINOR_UNION) {
				result += 6 * BONUS_ATTACK_MINOR * curAttacked_backup.maCount();
			} else {
				result += 6 * BONUS_ATTACK_MINOR * curAttacked_backup.oaCount();
				result += 4 * BONUS_ATTACK_MINOR * curAttacked_backup.knaCount();
			}
		}
		
		return result;
	}

	private int calcSafeAttacksScores(FieldAttacks toMove, FieldAttacks other) {
		
		int result = 0;
		if (IFieldsAttacks.MINOR_UNION) {
			if (toMove.maCount() > 0) {
				boolean safeToGo = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_OFFICER, Figures.TYPE_OFFICER, toMove.getId(), other.getId())) >= 0;
				if (safeToGo) {
					result += BONUS_CONTROL_MINOR * toMove.maCount();
				}
			}
		} else {
			if (toMove.knaCount() > 0) {
				boolean safeToGo = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_KNIGHT, Figures.TYPE_KNIGHT, toMove.getId(), other.getId())) >= 0;
				if (safeToGo) {
					result += BONUS_CONTROL_MINOR * toMove.knaCount();
				}
			}
			if (toMove.oaCount() > 0) {
				boolean safeToGo = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_OFFICER, Figures.TYPE_OFFICER, toMove.getId(), other.getId())) >= 0;
				if (safeToGo) {
					result += BONUS_CONTROL_MINOR * toMove.oaCount();
				}
			}
		}
		
		if (toMove.raCount() > 0) {
			boolean safeToGo = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_CASTLE, Figures.TYPE_CASTLE, toMove.getId(), other.getId())) >= 0;
			if (safeToGo) {
				result += BONUS_CONTROL_ROOK * toMove.raCount();
			}
		}
		
		if (toMove.qaCount() > 0) {
			boolean safeToGo = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_QUEEN, Figures.TYPE_QUEEN, toMove.getId(), other.getId())) >= 0;
			if (safeToGo) {
				result += BONUS_CONTROL_QUEEN * toMove.qaCount();
			}
		}
		
		return result;
	}

	private int calcAttacksScores(FieldAttacks state) {
		
		int result = 0;
		
		if (IFieldsAttacks.MINOR_UNION) {
			result += BONUS_ATTACK_MINOR * state.maCount();
		} else {
			result += BONUS_ATTACK_MINOR * state.knaCount();
			result += BONUS_ATTACK_MINOR * state.oaCount();
		}
		
		result += BONUS_ATTACK_ROOK * state.raCount();
		result += BONUS_ATTACK_QUEEN * state.qaCount();
		
		return result;
	}
}
