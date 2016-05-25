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
package bagaturchess.search.impl.rootsearch.sequential;


import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.search.impl.utils.SearchUtils;


public class Mediator_AlphaAndBestMoveWindow extends SearchMediatorProxy {
	
	
	private ISearchInfo lastinfo;
	
	private static int TRUST_WINDOW_BEST_MOVE_MULTIPLIER = 2;
	private static int TRUST_WINDOW_BEST_MOVE_MIN = 8;
	private static int TRUST_WINDOW_BEST_MOVE_MAX = 32;
	private int trustWindow_BestMove;
	
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MULTIPLIER = 1;
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MIN = 1;
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MAX = SearchUtils.getMateVal(1);
	private int trustWindow_AlphaAspiration;
	
	private VarStatistic best_moves_diffs_per_depth;
	private static int TRUST_WINDOW_MTD_STEP_MIN = 4;
	
	
	public Mediator_AlphaAndBestMoveWindow(ISearchMediator _parent, IRootSearch _rootSearch) {
		
		super(_parent);
		
		trustWindow_BestMove 		= TRUST_WINDOW_BEST_MOVE_MIN;
		trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MIN;
		
		best_moves_diffs_per_depth = new VarStatistic(false);
		best_moves_diffs_per_depth.addValue(1, 1);
	}
	
	
	public void changedMajor(ISearchInfo info) {
		
		if (!info.isUpperBound()) {
			
			if (lastinfo != null) {
				
				adjustTrustWindow_BestMove(info);
				
				adjustTrustWindow_AlphaAspiration(info);
				
				if (!info.isMateScore() && !lastinfo.isMateScore()) {
					int eval_diff = Math.abs(info.getEval() - lastinfo.getEval());
					best_moves_diffs_per_depth.addValue(eval_diff, eval_diff);
				}
				//dump("Mediator_AlphaAndBestMoveWindow Trust Window MTD Step set to " + getTrustWindow_MTD_Step());
			}
		
			lastinfo = info;
		}
		
		super.changedMajor(info);
		
	}


	private void adjustTrustWindow_BestMove(ISearchInfo info) {
		
		int cur_mtdTrustWindow = trustWindow_BestMove;
		
		if (cur_mtdTrustWindow < TRUST_WINDOW_BEST_MOVE_MIN) {
			cur_mtdTrustWindow = TRUST_WINDOW_BEST_MOVE_MIN;
		} else {
			
			if (lastinfo.getBestMove() == info.getBestMove()) {
				if (cur_mtdTrustWindow == 0) {
					//cur_mtdTrustWindow = MTD_TRUST_WINDOW_MIN;
					throw new IllegalStateException("cur_mtdTrustWindow == 0");
				} else {
					cur_mtdTrustWindow *= TRUST_WINDOW_BEST_MOVE_MULTIPLIER;
				}
			} else {
				cur_mtdTrustWindow /= TRUST_WINDOW_BEST_MOVE_MULTIPLIER;
			}
			
			if (cur_mtdTrustWindow < 0) {
				throw new IllegalStateException("cur_mtdTrustWindow=" + cur_mtdTrustWindow);
			}
		}
		
		/*if (cur_mtdTrustWindow > trustWindow_AlphaAspiration) {
			cur_mtdTrustWindow = trustWindow_AlphaAspiration;
		}*/
		
		if (cur_mtdTrustWindow > TRUST_WINDOW_BEST_MOVE_MAX) {
			cur_mtdTrustWindow = TRUST_WINDOW_BEST_MOVE_MAX;
		}
		
		trustWindow_BestMove = cur_mtdTrustWindow;
		
		//dump("Mediator_AlphaAndBestMoveWindow Trust Window Best Move set to " + trustWindow_BestMove);
	}
	
	
	private void adjustTrustWindow_AlphaAspiration(ISearchInfo info) {
		
		if (!info.isMateScore() && trustWindow_AlphaAspiration != TRUST_WINDOW_ALPHA_ASPIRATION_MAX) {
			
			trustWindow_AlphaAspiration += TRUST_WINDOW_ALPHA_ASPIRATION_MULTIPLIER * Math.max(1, Math.abs(info.getEval() - lastinfo.getEval()));
			
		} else {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MAX;
		}
		
		if (trustWindow_AlphaAspiration < 0) {
			throw new IllegalStateException("cur_mtdTrustWindow alpha =" + trustWindow_AlphaAspiration);
		}
		
		if (trustWindow_AlphaAspiration < TRUST_WINDOW_ALPHA_ASPIRATION_MIN) {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MIN;
		}
		
		if (trustWindow_AlphaAspiration > TRUST_WINDOW_ALPHA_ASPIRATION_MAX) {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MAX;
		}
		
		//dump("Mediator_AlphaAndBestMoveWindow Trust Window Alpha Aspiration set to " + trustWindow_AlphaAspiration);
	}
	
	
	@Override
	public int getTrustWindow_BestMove() {
		return trustWindow_BestMove;
	}
	
	
	@Override
	public int getTrustWindow_AlphaAspiration() {
		return trustWindow_AlphaAspiration;
	}
	
	
	@Override
	public int getTrustWindow_MTD_Step() {
		return TRUST_WINDOW_MTD_STEP_MIN;//(int) Math.max(TRUST_WINDOW_MTD_STEP_MIN, best_moves_diffs_per_depth.getEntropy());
	}
}
