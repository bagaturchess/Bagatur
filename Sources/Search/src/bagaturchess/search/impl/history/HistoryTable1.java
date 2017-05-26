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

import bagaturchess.bitboard.impl.movegen.MoveInt;


public class HistoryTable1 implements IHistoryTable {
	
	
	public HistoryTable1() {
		
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#countFailure(int)
	 */
	@Override
	public void countFailure(int move) {
		int pid = MoveInt.getFigurePID(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#countSuccess(int, int)
	 */
	@Override
	public void countSuccess(int move, int value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getScores(int)
	 */
	@Override
	public double getScores(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#addCounterMove(int, int)
	 */
	@Override
	public void addCounterMove(int last_move, int counter_move) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove1(int)
	 */
	@Override
	public int getCounterMove1(int last_move) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove2(int)
	 */
	@Override
	public int getCounterMove2(int last_move) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove3(int)
	 */
	@Override
	public int getCounterMove3(int last_move) {
		// TODO Auto-generated method stub
		return 0;
	}
}
