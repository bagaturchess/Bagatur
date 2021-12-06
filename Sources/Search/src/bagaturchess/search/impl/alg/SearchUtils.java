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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.internal.ISearch;


public class SearchUtils {
	
	
	public static final int normDepth(int maxdepth) {
		
		return maxdepth / ISearch.PLY;
	}
	
	
	public static final int getMateVal(int depth) {
		
		return ISearch.MAX_MAT_INTERVAL * (1 + ISearch.MAX_DEPTH - Math.min(ISearch.MAX_DEPTH, Math.max(0, depth)));
	}
	
	
	public static final boolean isMateVal(int val) {
		
		return Math.abs(val) >= ISearch.MAX_MAT_INTERVAL;
		//return Math.abs(val) != ISearch.MAX && Math.abs(val) >= ISearch.MAX_MAT_INTERVAL;
	}
	
	
	public static final int getMateDepth(int score) {
		
		if (score % ISearch.MAX_MAT_INTERVAL != 0) {
			
			throw new IllegalStateException();
		}
		
		if (score > ISearch.MAX) {
			
			throw new IllegalStateException();
		}
		
		if (score < ISearch.MIN) {
			
			throw new IllegalStateException();
		}
		
		//Between 1 and ISearch.MAX_DEPTH + 1
		score = Math.abs(score / ISearch.MAX_MAT_INTERVAL);
		
		int depth = 1 + (ISearch.MAX_DEPTH + 1) - score; 
		
		return score > 0 ? depth : -depth;
	}
	
	
	public static int getDrawScores(IBitBoard board, int root_player_colour) {
		
		if (root_player_colour != -1) {
			
			throw new IllegalStateException();
		}
		
		int scores = board.getMaterialFactor().interpolateByFactor(ISearch.DRAW_SCORE_O, ISearch.DRAW_SCORE_E);
		
		if (board.getColourToMove() != root_player_colour) {
			
			scores = -scores;
		}
		
		return scores;
	}
}
