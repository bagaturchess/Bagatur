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
package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;

import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVNode;


public class Search_PVS_NWS extends SearchImpl {
		
	
	public Search_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
		
		TTUtil.setSizeMB(64);
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		super(_env);
		
		TTUtil.setSizeMB(64);
	}
	
	
	public void newSearch() {
		
		super.newSearch();
		
		NegamaxUtil.start(((BoardImpl) env.getBitboard()).getChessBoard());
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		int result = NegamaxUtil.calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, 0, normDepth(maxdepth), alpha_org, beta, 0, true);
		
		PVNode node = pvman.load(0);
		node.bestmove = PV.getBestMove();
		node.eval = result;
		node.leaf = true;
		
		return result;
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return NegamaxUtil.calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, 0, normDepth(maxdepth), beta - 1, beta, 0, false);		
	}
}
