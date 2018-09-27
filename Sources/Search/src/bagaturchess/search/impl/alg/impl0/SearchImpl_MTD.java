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
package bagaturchess.search.impl.alg.impl0;


import bagaturchess.search.api.ISearchConfig_MTD;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.tpt.TPTEntry;


public abstract class SearchImpl_MTD extends SearchImpl {
	
	
	public SearchImpl_MTD(SearchEnv _env) {
		super(_env);
	}
	
	
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
		//return cur_eval > alpha;
	}
	
	
	public ISearchConfig_MTD getSearchConfig() {
		return (ISearchConfig_MTD) super.getSearchConfig();
	}
	
	
	@Override
	public void search(ISearchMediator mediator, int startIteration, int max_iterations, boolean useMateDistancePrunning) {
		
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
			mediator.registerInfoObject(info);
			
			info.setSearchedNodes(searchedNodesCount);
			info.setDepth(iteration);
			info.setSelDepth(iteration);
			
			int eval = 0;//searchMTD(mediator, info, PLY * iteration, lasteval, useMateDistancePrunning);
			
			searchedNodesCount = info.getSearchedNodes();			
			lasteval = eval;//Important line
		}
	}
}
