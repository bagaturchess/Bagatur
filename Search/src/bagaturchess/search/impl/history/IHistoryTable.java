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


import bagaturchess.bitboard.api.IHistoryProvider;


public interface IHistoryTable extends IHistoryProvider {
	
	
	//Cleanup and/or normalization
	public void clear();
	
	
	//Moves history
	public void registerAll(int color, int move, int depth);
	public void registerBad(int color, int move, int depth);
	public void registerGood(int color, int move, int depth);
	public int getScores(int color, int move);
	
	
	//Counter moves
	public void addCounterMove(int color, int last_move, int counter_move);
	public int getCounter1(int color, int parentMove);
	public int getCounter2(int color, int parentMove);
	
	
	//Killer moves
	public void addKillerMove(int color, int move, int ply);
	public int getKiller1(int color, int ply);
	public int getKiller2(int color, int ply);
	
}
