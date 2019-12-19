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
package bagaturchess.search.impl.utils;


import bagaturchess.search.api.internal.ISearch;


public class SearchUtils {
	
	public static final int getMateVal(int depth) {
		if (depth <= 0) {
			throw new IllegalStateException("depth=" + depth);
		}
		return ISearch.MAX_MAT_INTERVAL * (ISearch.MAX_DEPTH - depth + 1);
	}
	
	public static final int getMateVal_0(int depth) {
		if (depth < 0) {
			throw new IllegalStateException("depth=" + depth);
		}
		return ISearch.MAX_MAT_INTERVAL * (ISearch.MAX_DEPTH - depth + 1);
	}
	
	public static final boolean isMateVal(int val) {
		val = Math.abs(val);
		
		/*if (val > ISearch.MAX_MAT_INTERVAL) {
			if (val % ISearch.MAX_MAT_INTERVAL != 0) {
				throw new IllegalStateException("val=" + val + ", val % ISearch.MAX_MAT_INTERVAL=" + (val % ISearch.MAX_MAT_INTERVAL));
			}
		}*/
		
		return val != ISearch.MAX && val >= ISearch.MAX_MAT_INTERVAL;// && val % ISearch.MAX_MAT_INTERVAL == 0;
	}
	
	public static final int getMateDepth(int score) {
		int norm_score = Math.abs(score) / ISearch.MAX_MAT_INTERVAL;
		int depth =  ISearch.MAX_DEPTH + 1 - norm_score;
		depth = (depth + 1) / 2;
		return score > 0 ? depth : -depth;
	}
	
	public static final int normDepth(int maxdepth) {
		return maxdepth / ISearch.PLY;
	}
}
