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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;


class FieldSMGenerator implements Serializable {
	
	//private static final int STATE_ID_CODE_SHIFT_COLOUR = 0;
	//private static final int STATE_ID_CODE_SHIFT_TYPE = STATE_ID_CODE_SHIFT_COLOUR + 2;
	//private static final int STATE_ID_CODE_SHIFT_WHITEA = STATE_ID_CODE_SHIFT_TYPE + FieldAttacksSMGenerator.MIN_BIT_COUNT;
	//private static final int STATE_ID_CODE_SHIFT_BLACKA = STATE_ID_CODE_SHIFT_WHITEA + FieldAttacksSMGenerator.MIN_BIT_COUNT;
	
	private final int SIZE = 12
																  * FieldAttacksStateMachine.getInstance().getAllStatesList().length
																  * FieldAttacksStateMachine.getInstance().getAllStatesList().length;
	
	private ArrayList<FieldState> states = new ArrayList<FieldState>(SIZE);
	private HashMap<FieldState, FieldState> allStatesMap;
	
	
	FieldSMGenerator() {
		init();
	}
	
	public int statesCount() {
		return states.size();
	}
	
	public void generateStates() {
		int idSeq = 0;
		for (int colour=-1; colour<Figures.COLOUR_MAX; colour++) {
			if (colour == -1) { //Empty field
				for (int whiteAttacksID=0;
						whiteAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length;
						whiteAttacksID++) {
					for (int blackAttacksID=0;
					blackAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length;
					blackAttacksID++) {
						/*create(Figures.COLOUR_UNSPECIFIED, Figures.TYPE_UNDEFINED,StateMachinesGenerator_SingleColour.allStatesList[whiteAttacksID],
								StateMachinesGenerator_SingleColour.allStatesList[blackAttacksID]);*/
						FieldState state = new FieldState(idSeq++, Figures.COLOUR_UNSPECIFIED, Figures.TYPE_UNDEFINED,
								FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteAttacksID],
								FieldAttacksStateMachine.getInstance().getAllStatesList()[blackAttacksID]);
						states.add(state);
					}						
				}
			} else { //Figure on field
				for (int type=1; type<Figures.TYPE_MAX; type++) {
					for (int whiteAttacksID=0;
							whiteAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length;
							whiteAttacksID++) {
						for (int blackAttacksID=0;
						blackAttacksID < FieldAttacksStateMachine.getInstance().getAllStatesList().length;
						blackAttacksID++) {
							/*create(colour, type,StateMachinesGenerator_SingleColour.allStatesList[whiteAttacksID],
									StateMachinesGenerator_SingleColour.allStatesList[blackAttacksID]);*/
							FieldState state = new FieldState(idSeq++, colour, type,
									FieldAttacksStateMachine.getInstance().getAllStatesList()[whiteAttacksID],
									FieldAttacksStateMachine.getInstance().getAllStatesList()[blackAttacksID]);
							states.add(state);
						}						
					}
				}
			}
		}
	}
	
	/*private void create(int id, int figColour, int figType, FieldAttacks white, FieldAttacks black) {
		
		FieldState state = new FieldState(id, figColour, figType, white, black);
		states.add(state);
	}*/
	
	/*public static int genID(int figColour, int figType, FieldAttacks white, FieldAttacks black) {
		int id = 0;
		
		int colorCode = 0;
		if (figColour == Figures.COLOUR_UNSPECIFIED) {
			colorCode = 2;
		} else if (figColour == Figures.COLOUR_WHITE) {
			colorCode = 1;
		} else if (figColour == Figures.COLOUR_BLACK) {
			colorCode = 0;
		} else {
			throw new IllegalStateException("figColour=" + figColour);
		}
		
		id = id | (colorCode << STATE_ID_CODE_SHIFT_COLOUR);
		
		int typeCode = 0;
		switch (figType) {
			case Figures.TYPE_PAWN:
				typeCode = Figures.TYPE_PAWN;
				break;
			case Figures.TYPE_OFFICER:
				typeCode = Figures.TYPE_OFFICER;
				break;
			case Figures.TYPE_KNIGHT:
				typeCode = Figures.TYPE_KNIGHT;
				break;
			case Figures.TYPE_CASTLE:
				typeCode = Figures.TYPE_CASTLE;
				break;
			case Figures.TYPE_QUEEN:
				typeCode = Figures.TYPE_QUEEN;
				break;
			case Figures.TYPE_KING:
				typeCode = Figures.TYPE_KING;
				break;
			case Figures.TYPE_UNDEFINED:
				typeCode = 0;
				break;
			default:
				throw new IllegalStateException("figType=" + figType);
		}
		
		id = id | (typeCode << STATE_ID_CODE_SHIFT_TYPE);
		
		id = id | (white.getId() << STATE_ID_CODE_SHIFT_WHITEA);
		
		id = id | (black.getId() << STATE_ID_CODE_SHIFT_BLACKA);
		
		return id;
	}*/
	
	public void indexingStates() {
		allStatesMap = new HashMap<FieldState, FieldState>(2 * states.size());
		for (int i=0; i<states.size(); i++) {
			FieldState cur = states.get(i);
			allStatesMap.put(cur, cur);
			if (i % 100000 == 0) {
				System.out.print(".");
			}
		}
		System.out.print(" ");
	}
	
	public FieldStateMachine createStateMachine() {
		
		FieldStateMachine machine = FieldStateMachine.getInstanceForGen(states.size());
		
		//new FieldStateMachine(states.size(), true);
		
		for (int i=0; i<states.size(); i++) {
			
			FieldState cur = states.get(i);
			
			processSingleState(machine, FieldStateMachine.TRANSITION_ADD_ATTACK, Figures.COLOUR_WHITE, cur);
			processSingleState(machine, FieldStateMachine.TRANSITION_ADD_ATTACK, Figures.COLOUR_BLACK, cur);

			processSingleState(machine, FieldStateMachine.TRANSITION_REM_ATTACK, Figures.COLOUR_WHITE, cur);
			processSingleState(machine, FieldStateMachine.TRANSITION_REM_ATTACK, Figures.COLOUR_BLACK, cur);
			
			processSingleState(machine, FieldStateMachine.TRANSITION_ADD_FIGURE, Figures.COLOUR_WHITE, cur);
			processSingleState(machine, FieldStateMachine.TRANSITION_ADD_FIGURE, Figures.COLOUR_BLACK, cur);
			
			processSingleState(machine, FieldStateMachine.TRANSITION_REM_FIGURE, Figures.COLOUR_WHITE, cur);
			processSingleState(machine, FieldStateMachine.TRANSITION_REM_FIGURE, Figures.COLOUR_BLACK, cur);
			
			if (i % 100000 == 0) {
				System.out.print(".");
			}
		}
		System.out.print(" ");
		
		return machine;
	}

	private void processSingleState(FieldStateMachine machine, int op, int colour, FieldState cur) {
		for (int type=1; type<Figures.TYPE_MAX; type++) {
			FieldState next = FieldStateFactory.modify(op, colour, type, cur);
			if (next != null) {
				next = allStatesMap.get(next);
				machine.getMachine()[colour][type][op][cur.id] = next.id;
			} else {
				machine.getMachine()[colour][type][op][cur.id] = -1;
			}
		}
	}
	
	public void init() {
		System.out.print("Start states generation ... ");
		long start = System.currentTimeMillis();
		generateStates();
		long end = System.currentTimeMillis();
		
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		
		long usedMemory = max - (free + (max - total));
		System.out.println("Ready!");
		System.out.println("Time " + (end-start) + "ms");
		System.out.println("Used memory " + (usedMemory / (1024 * 1024)) + "MB");
		System.out.println("Total States Count: " + states.size());
		
		System.out.print("\r\n");
		
		System.out.print("Indexing ");
		start = System.currentTimeMillis();
		indexingStates();
		end = System.currentTimeMillis();
		
		max = Runtime.getRuntime().maxMemory();
		total = Runtime.getRuntime().totalMemory();
		free = Runtime.getRuntime().freeMemory();
		
		usedMemory = max - (free + (max - total));
		System.out.println("Ready!");
		System.out.println("Time " + (end-start) + "ms");
		System.out.println("Used memory " + (usedMemory / (1024 * 1024)) + "MB");
		System.out.println("Total States Count: " + allStatesMap.size());
		
		System.out.print("\r\n");
		
		System.out.print("Creating StateMachine ");
		FieldStateMachine machine = createStateMachine();
		machine.setStates(states);
		System.out.println("Ready!");
		
		System.out.print("Serializing ... ");
		machine.serialize();
		System.out.println("Ready!");
	}
	
	public static void main(String[] args) {
		FieldSMGenerator generator = new FieldSMGenerator();
		
		/*try {
			generator.init();
		} catch (Throwable t) {
			t.printStackTrace();	
		}*/
	}
}
