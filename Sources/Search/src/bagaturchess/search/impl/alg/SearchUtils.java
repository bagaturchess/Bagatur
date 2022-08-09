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
package bagaturchess.search.impl.alg;


import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.search.api.internal.ISearch;


public class SearchUtils {
	
	
	static {
		
		//System.out.println("SearchUtils.static: ISearch.MAX=" + ISearch.MAX);
		
		//System.out.println("SearchUtils.static: ISearch.MIN=" + ISearch.MIN);
		
		for (int depth = 1; depth <= ISearch.MAX_DEPTH; depth++) {
			
			int mate_val = getMateVal(depth);
			
			//System.out.println("SearchUtils.static: depth=" + depth + ", mate_val=" + mate_val);
			
			if (!isMateVal(mate_val)) {
				
				throw new IllegalStateException("SearchUtils.static: !isMateVal(mate_val), mate_val=" + mate_val);
			}
			
			int test_depth = getMateDepth(mate_val);
			
			if (test_depth != depth) {
			
				throw new IllegalStateException("SearchUtils.static: test_depth != depth, test_depth=" + test_depth + ", depth=" + depth);
			}
		}
	}
	
	
	public static final int getMateVal(int depth) {
		
		if (depth <= 0) {
			
			throw new IllegalStateException("SearchUtils.getMateVal: depth=" + depth);
		}
		
		return ISearch.MAX_MAT_INTERVAL * (1 + ISearch.MAX_DEPTH - Math.min(ISearch.MAX_DEPTH, Math.max(0, depth)));
	}
	
	
	public static final boolean isMateVal(int val) {
		
		return Math.abs(val) >= ISearch.MAX_MAT_INTERVAL;
		//return Math.abs(val) != ISearch.MAX && Math.abs(val) >= ISearch.MAX_MAT_INTERVAL;
	}
	
	
	public static final int getMateDepth(int score) {
		
		if (score % ISearch.MAX_MAT_INTERVAL != 0) {
			
			//throw new IllegalStateException("SearchUtils.getMateDepth: score % ISearch.MAX_MAT_INTERVAL != 0, score=" + score);
			//TODO: check why this is thrown
			//More info:
			/*info string java.lang.IllegalStateException: SearchUtils.getMateDepth: score % ISearch.MAX_MAT_INTERVAL != 0, score=-12700008
					info string 	at bagaturchess.search.impl.alg.SearchUtils.getMateDepth(Unknown Source)
					info string 	at bagaturchess.search.impl.info.SearchInfoImpl.getMateScore(Unknown Source)
					info string 	at bagaturchess.search.api.internal.SearchInfoUtils.buildMajorInfoCommand(Unknown Source)
					info string 	at bagaturchess.search.api.internal.SearchInfoUtils.buildMajorInfoCommand(Unknown Source)
					info string 	at bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_Base.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.utils.SearchMediatorProxy.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.utils.SearchMediatorProxy.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.SearchManager.increaseLowerBound(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.NullwinSearchTask.run(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD$1.run(Unknown Source)
					info string 	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
					info string 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
					info string 	at java.lang.Thread.run(Unknown Source)
					*/
			
			if (score >= 0) {
				
				score += ISearch.MAX_MAT_INTERVAL / 2;
				
			} else {
				
				score -= ISearch.MAX_MAT_INTERVAL / 2;
			}
		}
		
		if (score > ISearch.MAX) {
			
			//throw new IllegalStateException("SearchUtils.getMateDepth: score > ISearch.MAX, score=" + score);
			
			score = ISearch.MAX;
		}
		
		if (score < ISearch.MIN) {
			
			//throw new IllegalStateException("SearchUtils.getMateDepth: score < ISearch.MIN, score=" + score);
			
			/*info string java.lang.IllegalStateException: SearchUtils.getMateDepth: score < ISearch.MIN, score=-12812144
					info string 	at bagaturchess.search.impl.alg.SearchUtils.getMateDepth(Unknown Source)
					info string 	at bagaturchess.search.impl.info.SearchInfoImpl.getMateScore(Unknown Source)
					info string 	at bagaturchess.search.api.internal.SearchInfoUtils.buildMajorInfoCommand(Unknown Source)
					info string 	at bagaturchess.search.api.internal.SearchInfoUtils.buildMajorInfoCommand(Unknown Source)
					info string 	at bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_Base.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.utils.SearchMediatorProxy.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.utils.SearchMediatorProxy.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator.changedMajor(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.SearchManager.increaseLowerBound(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.mtd.NullwinSearchTask.run(Unknown Source)
					info string 	at bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD$1.run(Unknown Source)
					info string 	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
					info string 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
					info string 	at java.lang.Thread.run(Unknown Source)
			*/
					
			score = ISearch.MIN;
		}
		
		//Between 1 and ISearch.MAX_DEPTH + 1
		int mate_depth = Math.abs(score / ISearch.MAX_MAT_INTERVAL);
		
		int depth = (ISearch.MAX_DEPTH + 1) - mate_depth; 
		
		return score > 0 ? depth : -depth;
	}
	
	
	public static int getDrawScores(IMaterialFactor interpolater_by_material_facotr, int root_player_colour) {
		
		if (root_player_colour != -1) {
			
			throw new IllegalStateException();
		}
		
		int scores = interpolater_by_material_facotr.interpolateByFactor(ISearch.DRAW_SCORE_O, ISearch.DRAW_SCORE_E);
		
		/*if (board.getColourToMove() != root_player_colour) {
			
			scores = -scores;
		}*/
		
		return scores;
	}
	
	
	public static final int normDepth(int maxdepth) {
		
		return maxdepth / ISearch.PLY;
	}
}
