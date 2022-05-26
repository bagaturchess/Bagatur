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
package bagaturchess.search.impl.tpt;


import java.util.List;


public class TranspositionTableProvider implements ITTable {
	
	
	private List<ITTable> tpts;
	
	
	public TranspositionTableProvider(List<ITTable> _tpts) {
		
		tpts = _tpts;
	}
	
	
	public ITTable getTPT() {
		
		return this;
	}


	@Override
	public void correctAllDepths(int reduction) {
		
		//Do nothing
	}
	
	
	public void clear() {
		
		tpts.clear();
	}


	@Override
	public void get(long key, ITTEntry entry) {
		
		entry.setIsEmpty(true);
		
		for (int i = 0; i < tpts.size(); i++) {
			
			ITTable current_tpt = tpts.get(i);
			
			current_tpt.get(key, entry);
			
			if (!entry.isEmpty()) {
				
				break;
			}
		}
	}

	
	@Override
	public void put(long hashkey, int depth, int eval, int alpha, int beta, int bestmove) {
		
		for (int i = 0; i < tpts.size(); i++) {
			
			ITTable current_tpt = tpts.get(i);
			
			current_tpt.put(hashkey, depth, eval, alpha, beta, bestmove);
		}
	}


	@Override
	public int getUsage() {
		
		int total_usage = 0;
		
		for (int i = 0; i < tpts.size(); i++) {
			
			ITTable current_tpt = tpts.get(i);
			
			total_usage += current_tpt.getUsage();
		}
		
		return total_usage / tpts.size();
	}
	
	
	@Override
	public int getHitRate() {
		
		int total_hitrate = 0;
		
		for (int i = 0; i < tpts.size(); i++) {
			
			ITTable current_tpt = tpts.get(i);
			
			total_hitrate += current_tpt.getHitRate();
		}
		
		return total_hitrate / tpts.size();
	}
}
