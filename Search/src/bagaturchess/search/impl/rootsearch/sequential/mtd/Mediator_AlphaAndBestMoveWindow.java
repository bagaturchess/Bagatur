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
package bagaturchess.search.impl.rootsearch.sequential.mtd;


import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.utils.SearchMediatorProxy;


public class Mediator_AlphaAndBestMoveWindow extends SearchMediatorProxy {
	
	
	/**
	 * Table used for optimization of parameters by test games
	 * TRUST_WINDOW_BEST_MOVE_MIN, TRUST_WINDOW_BEST_MOVE_MAX,  ELO
	 * 							8, 							64,	+0 (initial baseline)
	 * 							8, 							32,	+28
	 * 							8, 							16,	+35
	 * 							8, 							 8,	+35
	 * 							4, 							 8,	+28
	 * 							4, 							 4,	+7
	 */
	private static int TRUST_WINDOW_BEST_MOVE_MULTIPLIER = 2;
	private static int TRUST_WINDOW_BEST_MOVE_MIN = 8;
	private static int TRUST_WINDOW_BEST_MOVE_MAX = 16;
	private int trustWindow_BestMove;
	
	/**
	 * Table used for optimization of parameters by test games
	 * TRUST_WINDOW_ALPHA_ASPIRATION_MIN, ELO
	 * 							1, 		+0 (initial baseline)
	 * 							128,	+0
	 */
	private static double TRUST_WINDOW_ALPHA_ASPIRATION_MULTIPLIER = 0.5;
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MIN = 1;
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MAX = SearchUtils.getMateVal(1);
	private int trustWindow_AlphaAspiration;
	
	/**
	 * Table used for optimization of parameters by test games
	 * TRUST_WINDOW_MTD_STEP_MIN, ELO
	 * 						4, 		+0 (initial baseline)
	 * 						8,		+7
	 */
	private static int TRUST_WINDOW_MTD_STEP_MIN = 8;
	
	
	private VarStatistic best_moves_diffs_per_depth;
	private ISearchInfo lastinfo;
	
	private int minEval = Integer.MAX_VALUE;
	private int maxEval = Integer.MIN_VALUE;
	
	
	public Mediator_AlphaAndBestMoveWindow(ISearchMediator _parent) {
		
		super(_parent);
		
		trustWindow_BestMove 		= TRUST_WINDOW_BEST_MOVE_MIN;
		trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MIN;
		
		best_moves_diffs_per_depth = new VarStatistic(false);
		best_moves_diffs_per_depth.addValue(1, 1);
	}
	
	
	public void changedMajor(ISearchInfo info) {
		
		if (!info.isUpperBound()) {
			
			if (lastinfo != null) {
				
				adjustTrustWindow_BestMove(info, lastinfo);
				
				adjustTrustWindow_AlphaAspiration(info, lastinfo);
				
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


	private void adjustTrustWindow_BestMove(ISearchInfo info, ISearchInfo previnfo) {
		
		int cur_mtdTrustWindow = trustWindow_BestMove;
		
		if (cur_mtdTrustWindow < TRUST_WINDOW_BEST_MOVE_MIN) {
			cur_mtdTrustWindow = TRUST_WINDOW_BEST_MOVE_MIN;
		} else {
			
			if (previnfo.getBestMove() == info.getBestMove()) {
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
	
	
	private void adjustTrustWindow_AlphaAspiration(ISearchInfo info, ISearchInfo previnfo) {
		
		if (!info.isMateScore() && trustWindow_AlphaAspiration != TRUST_WINDOW_ALPHA_ASPIRATION_MAX) {
			
			minEval = Math.min(minEval, info.getEval());
			maxEval = Math.max(maxEval, info.getEval());
			trustWindow_AlphaAspiration = Math.max(1, maxEval - minEval);
			
			//trustWindow_AlphaAspiration += TRUST_WINDOW_ALPHA_ASPIRATION_MULTIPLIER * Math.max(1, Math.abs(info.getEval() - previnfo.getEval()));
			
			//trustWindow_AlphaAspiration = Math.max(1, Math.abs(info.getEval() - previnfo.getEval()));
			
			//System.out.println(trustWindow_AlphaAspiration);
			
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
