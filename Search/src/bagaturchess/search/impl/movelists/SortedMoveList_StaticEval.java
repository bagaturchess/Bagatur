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


public class SortedMoveList_StaticEval extends SortedMoveList_BaseImpl {
	
	
	private int eval_before;
	
	
	public SortedMoveList_StaticEval(int max, SearchEnv _env, int _ply, boolean onTheFlySorting) {
		
		super(max, _env, onTheFlySorting);
	}
	
	
	@Override
	public void clear() {
		
		super.clear();
		
		eval_before = env.getEval().fullEval(-1, -1, -1, -1);
	}
	
	
	@Override
	protected int getOrderingValue(int move) {
		
		env.getBitboard().makeMoveForward(move);
		
		int value_after = -env.getEval().fullEval(-1, -1, -1, -1);
		
		env.getBitboard().makeMoveBackward(move);
		
		return value_after - eval_before;
	}
}
