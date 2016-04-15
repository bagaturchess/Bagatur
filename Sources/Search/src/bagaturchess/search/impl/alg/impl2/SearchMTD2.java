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
package bagaturchess.search.impl.alg.impl2;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.search.api.ISearchConfig_MTD;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
//import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.alg.BetaGenerator;
import bagaturchess.search.impl.alg.BetaGeneratorFactory;
import bagaturchess.search.impl.alg.IBetaGenerator;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.pv.PVHistoryEntry;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;


public class SearchMTD2 extends SearchAB2 {
	
	protected static final boolean SEND_PV = true;
	
	private List<Integer> betas = new ArrayList<Integer>();
	
	public SearchMTD2(Object[] args) {
		super(args);
	}
	
	public SearchMTD2(SearchEnv _env) {
		super(_env);
	}
	
	/*public SearchMTD_PV(SearchEnv _env, ISearchMoveList rootMoves) {
		super(_env, rootMoves);
	}*/
	
	@Override
	public void newSearch() {
		env.getMoveListFactory().newSearch();
		//Do not call super.newSearch(), it is not necessary to reduce the depth of TPT table entries during MTD search.
	}
	
	@Override
	protected boolean isPVNode(int cur_eval, int best_eval, int alpha, int beta) {
		return cur_eval > best_eval;
	}
	
	@Override
	protected boolean isNonAlphaNode(int cur_eval, int best_eval, int alpha, int beta) {
		return true;
		//return cur_eval > best_eval;
	}
	
	@Override
	public void search(ISearchMediator mediator, int startIteration, int max_iterations, boolean useMateDistancePrunning) {
		
		env.getEval().beforeSearch();
		
		int start_iteration = USE_TPT_IN_ROOT ? sentFromTPT(mediator, startIteration) : 1;
		if (start_iteration < startIteration) {
			start_iteration = startIteration;
		}
		if (start_iteration < 1) {
			start_iteration = 1;
		}
		
		int lasteval = (int) env.getEval().fullEval(0, MIN, MAX, env.getBitboard().getColourToMove());
		TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
		if (tptEntry != null && tptEntry.getBestMove_lower() != 0) {
			lasteval = tptEntry.getLowerBound();
		}
		
		long searchedNodesCount = 0;
		
		for (int iteration = start_iteration; iteration <= max_iterations; iteration++) {
			
			mediator.startIteration(iteration);
			
			ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
			info.setSearchedNodes(searchedNodesCount);
			info.setDepth(iteration);
			info.setSelDepth(iteration);
			
			int eval = searchMTD(mediator, info, PLY * iteration, lasteval, useMateDistancePrunning);
			
			searchedNodesCount = info.getSearchedNodes();			
			lasteval = eval;//Important line
		}
	}
	
	protected int searchMTD(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int initial_eval, boolean useMateDistancePrunning) {
		return searchMTD(mediator, info, maxdepth, initial_eval, MIN, MAX, useMateDistancePrunning);
	}
	
	public int searchMTD(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int initial_eval, int initial_lower, int initial_upper, boolean useMateDistancePrunning) {
		
		//System.out.println("maxdepth=" + maxdepth);
		
		int beta = initial_eval;
		int lasteval = 0;
		
		//int lower = initial_lower;
		//int upper = initial_upper;
		
		int[] prevPV = null;
		PVHistoryEntry entry = env.getPVs().getPV(env.getBitboard().getHashKey());
		if (entry != null) {
			prevPV = env.getPVs().getPV(env.getBitboard().getHashKey()).getPv();
			//System.out.println("prevPV=" + prevPV);
		}
		
		IRootWindow rootWin = new RootWindowImpl();
		
		betas.clear();
		IBetaGenerator beta_gen = BetaGeneratorFactory.create(initial_eval, 1);
		beta_gen.increaseLower(initial_lower);
		beta_gen.decreaseUpper(initial_upper);
		
		
		//boolean first_time = true;
		while (beta_gen.getLowerBound() + mediator.getTrustWindow_BestMove() < beta_gen.getUpperBound()) {
			
			beta = beta_gen.genBetas().get(0);
			
			//int eval = nullwin_search(mediator, stopper, info, maxdepth, 0, beta, false, 0, 0);
			int eval = pv_search(mediator, rootWin, info, maxdepth, maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0, env.getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
			//int eval = root_search(mediator, stopper, info, maxdepth, 0, beta - 1, beta, getPrevPV());
			
			//System.out.println("eval=" + eval);
			
			if (eval >= beta) {
				//eval is lower bound
				beta_gen.increaseLower(eval);
				
				info.setPV(PVNode.convertPV(PVNode.extractPV(pvman.load(0))));
				info.setBestMove(info.getPV()[0]);
				info.setEval(eval);
				//info.setHashFull(env.getTPT().getUsage());
				
				if (eval > initial_eval) {
					storePrevPV(info);
					info.setEval(eval);
					if (SEND_PV && mediator != null) {
						if (eval > initial_lower && eval < initial_upper) {
							mediator.changedMajor(info);
						}
					}
				}
				lasteval = eval;
				
				//int window_size = getWindow(lower, upper, first_time, eval);
				//beta = lower + window_size;
			} else {
				//eval is upper bound
				//eval < beta <=> eval <= beta - 1 <=> eval <= alpha
				beta_gen.decreaseUpper(eval);
				
				//int window_size = getWindow(lower, upper, first_time, eval);
				//beta = upper - window_size;
			}
			
			//first_time = false;
		}
		
		if (lasteval <= initial_eval) {
			
			storePrevPV(info);
			info.setEval(lasteval);
			if (SEND_PV && mediator != null) {
				if (lasteval > initial_lower && lasteval < initial_upper) {
					/*info.setPV(PVNode.convertPV(PVNode.extractPV(pvman.load(0))));
					info.setBestMove(info.getPV()[0]);
					info.setEval(lasteval);
					info.setHashFull(env.getTPT().getUsage());*/
					mediator.changedMajor(info);
				}
			}
		}
		
		return lasteval;
	}

	private int getWindow(int lower, int upper, boolean first_time, int eval) {
		int window_size;
		if (isMateVal(eval)) {
			window_size = 1;
		} else {
			window_size = first_time ? FIRSTTIME_WINDOW : Math.max(1, (upper - lower) / 2);
		}
		return window_size;
	}
	
	public ISearchConfig_MTD getSearchConfig() {
		return (ISearchConfig_MTD) super.getSearchConfig();
	}
}
