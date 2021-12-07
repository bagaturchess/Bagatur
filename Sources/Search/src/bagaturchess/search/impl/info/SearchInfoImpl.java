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
package bagaturchess.search.impl.info;

import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.alg.SearchUtils;




public class SearchInfoImpl implements ISearchInfo {
	

	public int depth;
	public int seldepth;
	
	public long nodes;
	public long tbhits;
	
	public int eval;
	
	public int curmove;
	public int curnumber;
	public int bestmove;
	public int[] pv;
	
	public boolean isMateScore;
	public int mateScore;
	public boolean isLowerBound;
	public boolean isUpperBound;
	
	
	public long getTBhits() {
		return tbhits;
	}
	
	public int getBestMove() {
		return bestmove;
	}

	public int getCurrentMove() {
		return curmove;
	}

	public int getCurrentMoveNumber() {
		return curnumber;
	}

	public int getDepth() {
		return depth;
	}

	public int getEval() {
		return eval;
	}
	
	public int[] getPV() {
		return pv;
	}

	public long getSearchedNodes() {
		return nodes;
	}

	public int getSelDepth() {
		return seldepth;
	}

	public void setTBhits(long tbhits) {
		this.tbhits = tbhits;
	}
	
	public void setBestMove(int move) {
		bestmove = move;
	}
	
	public void setCurrentMove(int move) {
		curmove = move;
	}
	
	public void setCurrentMoveNumber(int number) {
		curnumber = number;
	}
	
	public void setDepth(int _depth) {
		depth = _depth;
	}
	
	public void setEval(int _eval) {
		eval = _eval;
	}
	
	public void setPV(int[] _pv) {
		pv = _pv;
	}
	
	public void setSelDepth(int _seldepth) {
		seldepth = _seldepth;
	}
	
	public void setSearchedNodes(long _nodes) {
		nodes = _nodes;
	}

	public int getMateScore() {
		//eval + ISearch.MAX_MAT_INTERVAL / 2
		return ((SearchUtils.getMateDepth(eval) + 1) / 2);
	}

	public boolean isMateScore() {
		return SearchUtils.isMateVal(eval);
	}

	@Override
	public boolean isUpperBound() {
		return isUpperBound;
	}

	@Override
	public void setUpperBound(boolean _isUpperBound) {
		isUpperBound = _isUpperBound;
	}

	@Override
	public boolean isLowerBound() {
		return isLowerBound;
	}

	@Override
	public void setLowerBound(boolean _isLowerBound) {
		isLowerBound = _isLowerBound;
	}
}
