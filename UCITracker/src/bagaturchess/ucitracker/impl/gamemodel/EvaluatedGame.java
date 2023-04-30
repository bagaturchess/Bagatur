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
package bagaturchess.ucitracker.impl.gamemodel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class EvaluatedGame implements Serializable {
	
	private static final long serialVersionUID = 9113184112413109365L;
	
	private int[] opening;
	private transient String rootEPD;
	private List<EvaluatedPosition> boardStates;
	
	public List<EvaluatedPosition> getBoardStates() {
		return boardStates;
	}
	
	public String getRootEPD() {
		return rootEPD;
	}
	
	public EvaluatedGame(String _rootEPD) {
		rootEPD = _rootEPD;
		boardStates = new ArrayList<EvaluatedPosition>(1);
	}
	
	public void addBoard(EvaluatedPosition boardEval) {
		boardStates.add(boardEval);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += "ROOT EPD: " + rootEPD + "\r\n";
		
		for (int i=0; i<boardStates.size(); i++) {
			result += boardStates.get(i) + "\r\n";
		}
			
		return result;
	}
	
	public int[] getOpening() {
		if (opening == null) {
			opening = new int[0];
		}
		return opening;
	}
	
	public void setOpening(int[] opening) {
		this.opening = opening;
	}
}
