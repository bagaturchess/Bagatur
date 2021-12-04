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


package bagaturchess.search.impl.env;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.history.HistoryTable_PieceTo;
import bagaturchess.search.impl.history.IHistoryTable;
import bagaturchess.search.impl.movelists.OrderingStatistics;
import bagaturchess.search.impl.movelists.SearchMoveListFactory;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.utils.Tactics;


public class SearchEnv {
	
	
	private SharedData shared;
	
	private IBitBoard bitboard;
	private IEvaluator eval;
	private Tactics tactics;
	
	private IEvalCache evalCache;
	private IEvalCache syzygyDTZCache;
	private PawnsEvalCache pawnsCache;
	private ITTable tpt;

	
	private IHistoryTable history_all;
	private IHistoryTable history_incheck;
	
	private ISearchMoveListFactory moveListFactory;
	
	protected OrderingStatistics orderingStatistics;


	public SearchEnv(IBitBoard _bitboard, SharedData _shared) {
		
		shared = _shared;
		bitboard = _bitboard;
		tactics = new Tactics(bitboard);
		
		//history = new HistoryTable_FromTo(new BinarySemaphore_Dummy());
		history_all = new HistoryTable_PieceTo(bitboard);
		history_incheck = new HistoryTable_PieceTo(bitboard);
		
		moveListFactory = new SearchMoveListFactory();
		
		orderingStatistics = new OrderingStatistics();
	}
	
	
	public OrderingStatistics getOrderingStatistics() {
		return orderingStatistics;
	}
	
	
	public ISearchMoveListFactory getMoveListFactory() {
		return moveListFactory;
	}
	
	
	public OpeningBook getOpeningBook() {
		return shared.getOpeningBook();
	}

	public IHistoryTable getHistory_All() {
		return history_all;
	}

	public IHistoryTable getHistory_InCheck() {
		return history_incheck;
	}
	
	public PawnsEvalCache getPawnsCache() {
		if (pawnsCache == null) {
			pawnsCache = shared.getAndRemovePawnsCache();
		}
		return pawnsCache;
	}
	
	public int getTPTUsagePercent() {
		if (tpt == null) {
			return 0;
		} else {
			return tpt.getUsage();
		}
	}
	
	
	public ITTable getTPT() {
		return shared.getTranspositionTableProvider().getTPT();
	}
	
	
	public ITTable getTPTQS() {
		throw new IllegalStateException();
	}
	
	
	public IBitBoard getBitboard() {
		if (bitboard.getPawnsCache() != getPawnsCache()) {
			bitboard.setPawnsCache(getPawnsCache());
		}
		return bitboard;
	}
	
	public IEvaluator getEval() {
		if (eval == null) {
			eval = shared.getEvaluatorFactory().create(bitboard, getEvalCache(), shared.getEngineConfiguration().getEvalConfig());
		}
		return eval;
	}

	public void setEval(IEvaluator _eval) {
		eval = _eval;
	}
	
	public void clear() {
		shared.clear();
	}

	public Tactics getTactics() {
		return tactics;
	}

	public IEvalCache getEvalCache() {
		if (evalCache == null) {
			evalCache = shared.getAndRemoveEvalCache();
		}
		return evalCache;
	}
	
	public IEvalCache getSyzygyDTZCache() {
		if (syzygyDTZCache == null) {
			syzygyDTZCache = shared.getSyzygyDTZCache();
		}
		return syzygyDTZCache;
	}
	
	public IRootSearchConfig getEngineConfiguration() {
		return shared.getEngineConfiguration();
	}
	
	public ISearchConfig_AB getSearchConfig() {
		return shared.getSearchConfig();
	}
	
	@Override
	public String toString() {
		
		String result = "";
		//result += shared.toString();
		result += "Eval Cache HIT RATE is: " + getEvalCache().getHitRate();
		result += "; Pawn Cache HIT RATE is: " + getPawnsCache().getHitRate();
		result += "\r\nMOVE ORDERING STATISTICS\r\n" + getMoveListFactory().toString();
		
		return result;
	}
}
