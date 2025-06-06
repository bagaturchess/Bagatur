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
	
	
	public SortedMoveList_History(int max, SearchEnv _env, boolean onTheFlySorting) {
		super(max, _env, onTheFlySorting);
	}
	
	
	@Override
	protected int getOrderingValue(int move) {
		
		IHistoryTable history = env.getBitboard().isInCheck() ? env.getHistory_InCheck() : env.getHistory_All();
		
		return history.getScores(env.getBitboard().getColourToMove(), move);
	}
}
