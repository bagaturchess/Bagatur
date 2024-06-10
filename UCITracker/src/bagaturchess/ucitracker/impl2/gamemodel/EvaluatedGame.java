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
package bagaturchess.ucitracker.impl2.gamemodel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class EvaluatedGame implements Serializable {
	
	private static final long serialVersionUID = 9113184112413109363L;
	
	float result; //From white point of view
	
	List<String> moves;
	
	List<String> fens;
	
	List<Integer> evals; //From white point of view
	
	public EvaluatedGame() {
		
		moves = new ArrayList<String>();
		fens = new ArrayList<String>();
		evals = new ArrayList<Integer>();
	}
	
	public void addBoard(String move, String fen, int eval) {
		moves.add(move);
		fens.add(fen);
		evals.add(eval);
	}
	
	public void setResult(float _result) {
		
		result = _result;
	}
}
