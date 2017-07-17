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
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.egtb.gaviota.GTBProbing;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.alg.iter.OrderingStatistics;
import bagaturchess.search.impl.alg.iter.SearchMoveListFactory;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.exts.Extensions;
import bagaturchess.search.impl.history.HistoryTable_FromTo;
import bagaturchess.search.impl.history.HistoryTable_PieceTo;
import bagaturchess.search.impl.history.IHistoryTable;
import bagaturchess.search.impl.pv.PVHistory;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.search.impl.utils.Tactics;


public class SearchEnv {
	
	
	private SharedData shared;
	
	private IBitBoard bitboard;
	private IEvaluator eval;
	private Tactics tactics;
	private Extensions extensions;
	
	private IEvalCache evalCache;
	private PawnsEvalCache pawnsCache;
	private TPTable tpt;
	private GTBCache_OUT egtb_cache;
	private GTBProbing gtb_probing;
	private boolean egtb_cache_get;
	private boolean gtb_probing_get;

	
	private IHistoryTable history_all;
	private IHistoryTable history_incheck;
	
	private ISearchMoveListFactory moveListFactory;
	private PVHistory pvs_history;
	
	protected OrderingStatistics orderingStatistics;


	public SearchEnv(IBitBoard _bitboard, SharedData _shared) {
		shared = _shared;
		bitboard = _bitboard;
		tactics = new Tactics(bitboard);
		
		//history = new HistoryTable_FromTo(new BinarySemaphore_Dummy());
		history_all = new HistoryTable_PieceTo();
		history_incheck = new HistoryTable_PieceTo();
		
		moveListFactory = new SearchMoveListFactory();
		
		pvs_history = new PVHistory();
		
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
	
	public PVHistory getPVs() {
		return pvs_history;
	}
	
	public int getTPTUsagePercent() {
		if (tpt == null) {
			return 0;
		} else {
			return tpt.getUsage();
		}
	}
	
	
	public TPTable getTPT() {
		if (tpt == null) {
			tpt = shared.getAndRemoveTPT();
		}
		return tpt;
	}
	
	
	public GTBCache_OUT getEGTBCache() {
		if (!egtb_cache_get) {
			egtb_cache = shared.getAndRemoveGTBCache_OUT();
			egtb_cache_get = true;
		}
		return egtb_cache;
	}
	
	
	/**
	 * Returns null, because the calls to GTBProbing.probe() cause jvm crashes on some hardwares as well as Java OOM errors
	 */
	public GTBProbing getGTBProbing() {
		
		if (!gtb_probing_get) {
			gtb_probing = shared.getAndRemoveGTBProbing();
			gtb_probing_get = true;
		}
		
		//return gtb_probing;
		return null;
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

	public Extensions getExtensions() {
		if (extensions == null) {
			extensions = new Extensions(getSearchConfig().getExtensionMode(), getSearchConfig().getDynamicExtUpdateInterval());
		}
		return extensions;
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
		result += "; Transposition Table HIT RATE is: " + getTPT().getHitRate();
		result += "\r\nMOVE ORDERING STATISTICS\r\n" + getMoveListFactory().toString();
		
		return result;
	}
}
