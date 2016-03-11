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
import java.util.List;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.StaticScores;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;


public class FieldStateMachine implements Serializable {
	
	private static final long serialVersionUID = 7412575151839070935L;

	private static final String STATE_MACHINE_LOCATION = "C:\\StateMachine.bin";
	
	public static final int TRANSITION_ADD_ATTACK = 0;
	public static final int TRANSITION_REM_ATTACK = 1;
	public static final int TRANSITION_ADD_FIGURE = 2;
	public static final int TRANSITION_REM_FIGURE = 3;
	public static final int TRANSITION_MAX_INDEX = TRANSITION_REM_FIGURE + 1;
	public static final String[] TRANSITION_SIGN = new String[TRANSITION_MAX_INDEX];
	
	static {
		TRANSITION_SIGN[TRANSITION_ADD_ATTACK] = "ADD_A"; 
		TRANSITION_SIGN[TRANSITION_REM_ATTACK] = "REM_A";
		TRANSITION_SIGN[TRANSITION_ADD_FIGURE] = "ADD_F";
		TRANSITION_SIGN[TRANSITION_REM_FIGURE] = "REM_F";
	}
	
	private static FieldStateMachine singleton;
	
	private int[][][][] machine;
	private List<FieldState> states;
	private StaticScores scores;
	
	public static final FieldStateMachine getInstance() {
		return getInstance(-1, false);
	}
	
	public static final FieldStateMachine getInstanceForGen(int statesCount) {
		return getInstance(statesCount, true);
	}
	
	private static final FieldStateMachine getInstance(int statesCount, boolean generate) {
		
		if (singleton == null) {
			synchronized (FieldAttacksStateMachine.class) {
				if (singleton == null) {
					singleton = new FieldStateMachine(statesCount, generate);
				}
			}
		}
		
		return singleton;
	}
	
	private FieldStateMachine(int statesCount, boolean generate) {

		if (generate) {
			machine = new int[Figures.COLOUR_MAX][Figures.TYPE_MAX][TRANSITION_MAX_INDEX][statesCount];
		} else {
			deserialize();
		}
	}
	
	public String stateToString(int id) {
		FieldState stateObj = states.get(id);
		
		String result = "";
		result += "ON=";
		if (stateObj.figureOnFieldColour == Figures.COLOUR_UNSPECIFIED) {
			result += "EMPTY";
		} else {
			result += Figures.COLOURS_SIGN[stateObj.figureOnFieldColour];
			result += Figures.TYPES_SIGN[stateObj.figureOnFieldType];
		}
		result += ", ";
		result += "WA=" + stateObj.whiteAttacks;
		result += ", ";
		result += "BA=" + stateObj.whiteAttacks;
		
		return result;
	}
	
	public int nextState(int colour, int type, int op, int currentState) {
		int next = machine[colour][type][op][currentState];
		return next;
	}
	
	public void serialize() {
		String file = FieldStateMachine.STATE_MACHINE_LOCATION;
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
			os.writeObject(this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deserialize() {
		String location = FieldStateMachine.STATE_MACHINE_LOCATION;
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(new FileInputStream(location));
			FieldStateMachine obj = (FieldStateMachine) is.readObject();
			machine = obj.machine;
			states = obj.states;
			scores = new StaticScores(this);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		try {
			System.out.println("Deserialize ...");
			FieldStateMachine fsm = new FieldStateMachine(-1, false);
			System.out.println("OK");
		} catch(Throwable t) {
			t.printStackTrace();
		}
		//System.out.println("fsm.size=" + fsm.machine);
	}

	public int[][][][] getMachine() {
		return machine;
	}

	public List<FieldState> getStates() {
		return states;
	}

	public void setStates(List<FieldState> states) {
		this.states = states;
	}

	public StaticScores getScores() {
		return scores;
	}
}
