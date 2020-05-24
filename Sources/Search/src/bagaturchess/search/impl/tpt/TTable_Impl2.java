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
package bagaturchess.search.impl.tpt;


public class TTable_Impl2 implements ITTable {
	
	
	public TTable_Impl2(int sizeInMB) {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void correctAllDepths(final int reduction) {
	}
	
	
	@Override
	public void get(long key, ITTEntry entry) {
		
		entry.setIsEmpty(true);
		
		/*TPTEntry mem = super.getAndUpdateLRU(key);
		
		if (mem != null) {
			
			entry.setIsEmpty(false);
			entry.setDepth(mem.getDepth());
			
			if (mem.isExact()) {
				entry.setFlag(ITTEntry.FLAG_EXACT);
				entry.setEval(mem.getLowerBound());
				entry.setBestMove(mem.getBestMove_lower());
			} else if (mem.getBestMove_lower() != 0) {
				entry.setFlag(ITTEntry.FLAG_LOWER);
				entry.setEval(mem.getLowerBound());
				entry.setBestMove(mem.getBestMove_lower());
			} else {
				entry.setFlag(ITTEntry.FLAG_UPPER);
				entry.setEval(mem.getUpperBound());
				entry.setBestMove(mem.getBestMove_upper());
			}
		}*/
	}
	
	
	@Override
	public void put(long hashkey,
			int _smaxdepth, int _sdepth, 
			int _colour,
			int _eval, int _alpha, int _beta, int _bestmove,
			byte movenumber) {

	}

	
	@Override
	public int getUsage() {
		return 0;
	}
}
