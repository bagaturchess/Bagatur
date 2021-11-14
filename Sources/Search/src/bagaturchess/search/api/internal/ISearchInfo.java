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


public interface ISearchInfo {

	public int getDepth();
	public void setDepth(int depth);
	
	public int getSelDepth();
	public void setSelDepth(int seldepth);
	
	public int getEval();
	public void setEval(int eval);
	
	public int getCurrentMove();
	public void setCurrentMove(int move);
	
	public int getBestMove();
	public void setBestMove(int move);
	
	public int[] getPV();
	public void setPV(int[] pv);
	
	public int getCurrentMoveNumber();
	public void setCurrentMoveNumber(int number);
	
	public long getSearchedNodes();
	public void setSearchedNodes(long nodes);
	
	public long getTBhits();
	public void setTBhits(long tbhits);
	
	public boolean isMateScore();
	
	public int getMateScore();
	
	public boolean isUpperBound();
	public void setUpperBound(boolean isUpperBound);
	
	public boolean isLowerBound();
	public void setLowerBound(boolean isUpperBound);
}
