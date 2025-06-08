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
package bagaturchess.search.impl.movelists;


import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.IHistoryTable;


public class SortedMoveList_History extends SortedMoveList_BaseImpl {
	
	
	private int ply;
	
	
	public SortedMoveList_History(int max, SearchEnv _env, int _ply, boolean onTheFlySorting) {
		
		super(max, _env, onTheFlySorting);
		
		ply = _ply;
	}
	
	
	@Override
	protected int getOrderingValue(int move) {
		
		int color = env.getBitboard().getColourToMove();
		
		int value = 0;
		
		if (SearchEnv.USE_HISTORIES_PER_PLY) {
			
			if (ply - 1 >= 0) {
				
				value += env.getHistoryPerPly()[ply - 1].getScores(color, move);
				
				if (ply - 2 >= 0) {
					
					value += env.getHistoryPerPly()[ply - 2].getScores(color, move);
					
					if (ply - 4 >= 0) {
						
						value += env.getHistoryPerPly()[ply - 4].getScores(color, move);
						
						if (ply - 6 >= 0) {
							
							value += env.getHistoryPerPly()[ply - 6].getScores(color, move);
						}
					}
				}
			}
		} else {
			
			IHistoryTable history = env.getHistory();
			
			value += history.getScores(color, move);
		}
		
		return value;
	}
}
