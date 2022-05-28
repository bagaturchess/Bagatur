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
package bagaturchess.search.api.internal;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;


public interface ISearch {
	
	
	public static int PLY 						= ISearchConfig_AB.PLY;
	
	public static final int MAX_MAT_INTERVAL 	= IEvaluator.MAX_EVAL;
	
	public static final int MAX_DEPTH 			= 128;
	
	public static final int MAX 				= ISearch.MAX_MAT_INTERVAL * ISearch.MAX_DEPTH; //+MATE in 1 move
	
	public static final int MIN 				= -MAX; //-MATE in 1 move
	
	public static final int DRAW_SCORE_O 		= 0;
	
	public static final int DRAW_SCORE_E 		= 0;
	
	
	public void newGame();
	
	public void newSearch();
	
	public void setup(IBitBoard bitboard);
	
	public SearchEnv getEnv();
	
	public int pv_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int alpha_org, int beta,
			int prevbest, int prevprevbest, int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove, int mateMove, boolean useMateDistancePrunning);
	
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info, int initial_maxdepth,
			int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV, int rootColour, int totalLMReduction, int materialGain, boolean inNullMove, int mateMove,
			boolean useMateDistancePrunning);
}
