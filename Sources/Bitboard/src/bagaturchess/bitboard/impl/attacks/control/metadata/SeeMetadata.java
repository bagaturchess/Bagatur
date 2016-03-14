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


import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;


public class SeeMetadata {
	
	private static final int COSTS_MULTIPLIER = 100;
	
	//opFigureType, playerToMoveFieldState, opPlayerFieldState 
	private static int STATES_COUNT = FieldAttacksStateMachine.getInstance().getAllStatesList().length;
	public static final byte[][][] FIELD_SEE = new byte[Figures.TYPE_MAX][STATES_COUNT][STATES_COUNT];
	
	private static final boolean init_field_blocked = false;
	public static int[][][] FIELD_BLOCKED;
	static {
		if (init_field_blocked) {
			FIELD_BLOCKED = new int[Figures.TYPE_MAX][STATES_COUNT][STATES_COUNT];
		}
	}
	//public static final int[][][] SAFE_FOR = new int[Figures.TYPE_MAX][STATES_COUNT][STATES_COUNT];
	//public static final int[][][] HANGING = new int[Figures.TYPE_MAX][STATES_COUNT][STATES_COUNT];
	
	public static final int[][] TYPE_LISTS = new int[STATES_COUNT][];
	public static final int[][] COST_LISTS = new int[STATES_COUNT][];
	
	private static SeeMetadata singleton;
	
	private SeeMetadata() {
		init();
	}
	
	public static final SeeMetadata getSingleton() {
		if (singleton == null) {
			synchronized (SeeMetadata.class) {
				if (singleton == null) {
					singleton = new SeeMetadata();
				}
			}
		}
		
		return singleton;
	}

	private static void init() {
		for (int figType=0; figType<Figures.TYPE_MAX; figType++) {
			
			if (figType != Figures.TYPE_PAWN
					&& figType != Figures.TYPE_OFFICER
					&& figType != Figures.TYPE_KNIGHT
					&& figType != Figures.TYPE_CASTLE
					&& figType != Figures.TYPE_QUEEN
					&& figType != Figures.TYPE_KING
					) {
				continue;
			}
			
			for (int state=0; state<STATES_COUNT; state++) {
				FieldAttacks stateObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[state];
				TYPE_LISTS[state] = buildTypeList(stateObj);
				COST_LISTS[state] = buildCostList(TYPE_LISTS[state]);
			}
			
			for (int mystate=0; mystate<STATES_COUNT; mystate++) {
				for (int toPlayState=0; toPlayState<STATES_COUNT; toPlayState++) {
					
					int see = -seeField(figType, mystate, toPlayState);
					FIELD_SEE[figType][mystate][toPlayState] = (byte) (see / COSTS_MULTIPLIER);
					
					if (see < 0 && FIELD_SEE[figType][mystate][toPlayState] > 0) {
						throw new IllegalStateException();
					}
				}
			}
			
			if (init_field_blocked) {
				for (int mystate=0; mystate<STATES_COUNT; mystate++) {
					for (int toPlayState=0; toPlayState<STATES_COUNT; toPlayState++) {
						if (figType == 0) {
							FIELD_BLOCKED[figType][mystate][toPlayState] = 0;
						} else {
							FIELD_BLOCKED[figType][mystate][toPlayState] = fieldBlock(figType, mystate, toPlayState);
						}
					}
				}
			}
		}
	}
	
	public int seeMove(int typeToExclude, int myFigType, int mystate, int toPlayState) {
		int value = 0;
		
		if (typeToExclude != Figures.TYPE_UNDEFINED) {
			mystate = FieldAttacksStateMachine.getInstance().getMachine()[IFieldsAttacks.OP_REM_ATTACK][typeToExclude][mystate];
		}
		
		if (mystate < 0 || toPlayState < 0 || myFigType < 0) {
			System.out.println("myFigType=" + myFigType + ", mystate=" + mystate + ", toPlayState=" + toPlayState);
		}
		
		//value = seeField(myFigType, mystate, opstate);
		value = COSTS_MULTIPLIER * FIELD_SEE[myFigType][mystate][toPlayState];
		
		return value;
	}

	//toPlayState - field state from player to move point of view
	//myFigCost - cost of the figure laying on the field
	public static int seeField(int myFigType, int mystate, int toPlayState) {
		int value = 0;
		
		//FieldAttacks opstateObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[opstate];
		//FieldAttacks mystateObj = FieldAttacksStateMachine.getInstance().getAllStatesList()[mystate];
		int[] opcosts = COST_LISTS[toPlayState];//buildCostList(opstateObj);
		int[] mycosts = COST_LISTS[mystate];//buildCostList(mystateObj);
		
		value = -see(getCost(myFigType), 0, opcosts.length, opcosts, 0, mycosts.length, mycosts);
		
		return value;
	}
	
	private static int getCost(int type) {
		//return Constants.getFigureCost(type);
		if (type == Figures.TYPE_KING) {
			return 1500;
		} else {
			return BaseEvalWeights.getFigureMaterialSEE(type);
		}
	}
	
	private static int fieldBlock(int figType, int mystate, int toPlayState) {
		
		if (FIELD_SEE[figType][mystate][toPlayState] >= 0) { //toPlay wins the initiated capture sequence
			return 0;
		}
		
		if (FIELD_SEE[figType][mystate][toPlayState] >= 0) {
			throw new IllegalStateException();
		}
		
		int result = 0;
		
		int[] opcosts = COST_LISTS[toPlayState];//buildCostList(opstateObj);
		int[] mycosts = COST_LISTS[mystate];//buildCostList(mystateObj);
		
		//value = -see(Constants.getFigureCost(myFigType), 0, opcosts.length, opcosts, 0, mycosts.length, mycosts);
		
		return result;
	}
	
	private static int see(int currentPieceOnFieldCost, int playing_cur1, int playing_size1, int[] playing_l1, int cur2, int size2, int[] l2) {
		int value = 0;
		
		if (playing_size1 == 0
				|| playing_cur1 >= playing_size1) {
			return 0;
		}
		
		int curValue = currentPieceOnFieldCost - see(playing_l1[playing_cur1], cur2, size2, l2, playing_cur1 + 1, playing_size1, playing_l1);
		if (curValue > value) {
			value = curValue;
		}
		
		return value;
	}
	
	private static int[] buildCostList(int[] typeList) {
		
		int[] result = new int[typeList.length];
		for (int i=0; i<typeList.length; i++) {
			result[i] = getCost(typeList[i]);
		}
		
		/*int size = -1;
		if (IFieldsAttacks.MINOR_UNION) {
			size = fa.paCount() + fa.maCount() + fa.raCount() + fa.qaCount() + fa.kaCount();
		} else {
			size = fa.paCount() + fa.knaCount() + fa.oaCount() + fa.raCount() + fa.qaCount() + fa.kaCount();
		}
		
		int[] result = new int[size];
		
		int count = 0;
		
		for (int i=0; i<fa.paCount(); i++) {
			result[count++] = Constants.getFigureCost(Figures.TYPE_PAWN);
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			for (int i=0; i<fa.maCount(); i++) {
				if (Constants.getFigureCost(Figures.TYPE_KNIGHT) != Constants.getFigureCost(Figures.TYPE_OFFICER)) {
					throw new IllegalStateException();
				}
				result[count++] = Constants.getFigureCost(Figures.TYPE_KNIGHT);
			}
		} else {
			for (int i=0; i<fa.knaCount(); i++) {
				result[count++] = Constants.getFigureCost(Figures.TYPE_KNIGHT);
			}
			for (int i=0; i<fa.oaCount(); i++) {
				result[count++] = Constants.getFigureCost(Figures.TYPE_OFFICER);
			}
		}
		
		for (int i=0; i<fa.raCount(); i++) {
			result[count++] = Constants.getFigureCost(Figures.TYPE_CASTLE);
		}
		for (int i=0; i<fa.qaCount(); i++) {
			result[count++] = Constants.getFigureCost(Figures.TYPE_QUEEN);
		}
		for (int i=0; i<fa.kaCount(); i++) {
			result[count++] = Constants.getFigureCost(Figures.TYPE_KING);
		}*/
		
		return result;
	}
	
	private static int[] buildTypeList(FieldAttacks fa) {
		
		int size = -1;
		
		if (IFieldsAttacks.MINOR_UNION) {
			size = fa.paCount() + fa.maCount() + fa.raCount() + fa.qaCount() + fa.kaCount();
		} else {
			size = fa.paCount() + fa.knaCount() + fa.oaCount() + fa.raCount() + fa.qaCount() + fa.kaCount();
		}
		
		int[] result = new int[size];
		
		int count = 0;
		
		for (int i=0; i<fa.paCount(); i++) {
			result[count++] = Figures.TYPE_PAWN;
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			for (int i=0; i<fa.maCount(); i++) {
				result[count++] = Figures.TYPE_KNIGHT;
			}
		} else {
			for (int i=0; i<fa.knaCount(); i++) {
				result[count++] = Figures.TYPE_KNIGHT;
			}
			for (int i=0; i<fa.oaCount(); i++) {
				result[count++] = Figures.TYPE_OFFICER;
			}
		}
		
		for (int i=0; i<fa.raCount(); i++) {
			result[count++] = Figures.TYPE_CASTLE;
		}
		for (int i=0; i<fa.qaCount(); i++) {
			result[count++] = Figures.TYPE_QUEEN;
		}
		for (int i=0; i<fa.kaCount(); i++) {
			result[count++] = Figures.TYPE_KING;
		}
		
		return result;
	}
}
