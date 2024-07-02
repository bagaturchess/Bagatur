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
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.search.api.internal.ISearch;


public class SearchUtils {
	
	
	static {
		
		if (isMateVal(ISearch.MIN)) {
			
			throw new IllegalStateException("isMateVal(MIN)");
		}

		if (isMateVal(ISearch.MAX)) {
			
			throw new IllegalStateException("isMateVal(MAX)");
		}
		
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
	
	
	public static final int getMateVal(int depth, IBitBoard bitboard) {
		
		if (depth <= 0) {
			
			throw new IllegalStateException("SearchUtils.getMateVal: depth=" + depth + ", board=" + bitboard.toEPD());
		}
		
		return ISearch.MAX_MATERIAL_INTERVAL * (ISearch.MAX_DEPTH + 1 - depth);
	}
	
	
	public static final int getMateVal(int depth) {
		
		if (depth <= 0) {
			
			throw new IllegalStateException("SearchUtils.getMateVal: depth=" + depth);
		}
		
		return ISearch.MAX_MATERIAL_INTERVAL * (ISearch.MAX_DEPTH + 1 - depth);
	}
	
	
	public static final boolean isMateVal(int val) {
		
		return Math.abs(val) >= ISearch.MAX_MATERIAL_INTERVAL
				&& Math.abs(val) <= ISearch.MAX_MATE_INTERVAL
				&& Math.abs(val) % ISearch.MAX_MATERIAL_INTERVAL == 0;
		//return Math.abs(val) >= ISearch.MAX_MATERIAL_INTERVAL;
	}
	
	
	public static final int getMateDepth(int score) {
		
		if (!isMateVal(score)) {
			
			throw new IllegalStateException("!isMateVal(score)");
		}
		
		if (Math.abs(score) % ISearch.MAX_MATERIAL_INTERVAL != 0) {
			
			throw new IllegalStateException("Math.abs(score) % ISearch.MAX_MATERIAL_INTERVAL != 0");
		}
		
		if (score == ISearch.MIN) {
			
			throw new IllegalStateException("score == ISearch.MIN");
		}
		
		if (score == ISearch.MAX) {
			
			throw new IllegalStateException("score == ISearch.MAX");
		}
		
		
		if (score > ISearch.MAX_MATE_INTERVAL) {
			
			throw new IllegalStateException("score > ISearch.MAX_MATE_INTERVAL");
		}
		
		if (score < -ISearch.MAX_MATE_INTERVAL) {
					
			throw new IllegalStateException("score < -ISearch.MAX_MATE_INTERVAL");
		}
		
		//Between 1 and ISearch.MAX_DEPTH
		int mate_depth = Math.abs(score / ISearch.MAX_MATERIAL_INTERVAL);
		
		int depth = 1 + ISearch.MAX_DEPTH - mate_depth; 
		
		if (depth <= 0) {
			
			throw new IllegalStateException("depth=" + depth);
		}
		
		return score > 0 ? depth : -depth;
	}
	
	
	public static int getDrawScores(IMaterialFactor interpolater_by_material_facotr, int root_player_colour) {
		
		if (root_player_colour != -1) {
			
			//throw new IllegalStateException();
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
