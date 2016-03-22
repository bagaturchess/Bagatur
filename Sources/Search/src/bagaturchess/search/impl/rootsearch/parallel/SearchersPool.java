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
package bagaturchess.search.impl.rootsearch.parallel;


import java.util.Stack;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl1.Board3;
import bagaturchess.bitboard.impl1.Board3_Adapter;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.impl0.SearchMTD0;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.env.SharedData;


public class SearchersPool {
	
	
	private Stack<ISearch> pool = new Stack<ISearch>();
	private int searchersCount = 0;
	private IBoardConfig boardConfig;
	
	
	public SearchersPool(IBoardConfig _boardConfig) {
		boardConfig = _boardConfig;
	}
	
	
	public ISearch getSearcher(IBitBoard bitboardForSetup, SharedData sharedData) {
		
		if (searchersCount <= 0) {
			throw new IllegalAccessError("searchersCount=" + searchersCount);
		}
		
		ISearch result = pool.pop();
		result.setup(bitboardForSetup);
		
		return result;
	}
	
	
	public void releaseSearcher(ISearch searcher) {
		pool.push(searcher);
	}
	
	
	void newGame(IBitBoard bitboardForSetup, SharedData sharedData, int threadsCount) {
		
		searchersCount = threadsCount;
		
		pool.clear();
		
		for (int i=0; i<searchersCount; i++) {
			//IBitBoard searcher_bitboard = new Board(bitboardForSetup.toEPD(), boardConfig);
			
			int movesCount = bitboardForSetup.getPlayedMovesCount();
			int[] moves = Utils.copy(bitboardForSetup.getPlayedMoves());
			
			bitboardForSetup.revert();
			
			IBitBoard searcher_bitboard = null;
			//searcher_bitboard = new Board3_Adapter(bitboardForSetup.toEPD(), boardConfig);
			searcher_bitboard = BoardUtils.createBoard_WithPawnsCache(bitboardForSetup.toEPD(), boardConfig);
			
			for (int j=0; j<movesCount; j++) {
				bitboardForSetup.makeMoveForward(moves[j]);
				searcher_bitboard.makeMoveForward(moves[j]);
			}
			
			//searcher_bitboard.setAttacksSupport(true, true);
			SearchEnv searchEnv = new SearchEnv(searcher_bitboard, sharedData);
			ISearch searcher = new SearchMTD0(searchEnv);
			//ISearch searcher = new SearchBagatur(searchEnv);
			//ISearch searcher = new SearchLazy(searchEnv);
			releaseSearcher(searcher);
		}
	}
	
	
	public void dumpSearchers(ISearchMediator mediator) {
		for (int i=0; i<pool.size(); i++) {
			mediator.dump(pool.get(i).toString());
		}
	}
	
	
	@Override
	public String toString() {
		String result = "";
		for (int i=0; i<pool.size(); i++) {
			result += pool.get(i).toString();
			result += "\r\n";
		}
		return result;
	}
	
	
	void newSearch() {
		for (int i=0; i<pool.size(); i++) {
			pool.get(i).newSearch();
		}
	}
	
	
	void waitSearchersToStop() {
		while (true) {
			synchronized (pool) {
				if (pool.size() == searchersCount) {
					return;
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {}
			}
		}
	}
}
