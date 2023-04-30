/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.search.impl.history;


public interface IHistoryTable {
	
	
	//Cleanup and/or normalization
	public void newSearch();
	
	
	//Moves history
	public void countFailure(int move, int depth);
	public void countSuccess(int move, int depth);
	public double getScores(int move);//Returns value in [0, 1]. Bigger value means better move.
	
	
	//Counter moves
	public void addCounterMove(int last_move, int counter_move);
	public boolean isCounterMove(int last_move, int move);
}
