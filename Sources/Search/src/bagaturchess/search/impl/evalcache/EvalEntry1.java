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
package bagaturchess.search.impl.evalcache;


import bagaturchess.search.api.internal.ISearch;


public class EvalEntry1 implements IEvalEntry {
	
	
	private static final int MIN_VALUE = ISearch.MIN;
	private static final int MAX_VALUE = ISearch.MAX;
	
	
	byte level;
	int eval;
	
	
	public EvalEntry1(int _max_level) {
	}

	
	public String toString() {
		String result = "";
		
		result += " level=" + level;
		result += ", eval=" + eval;
		
		return result;
	}
	
	
	public void init(int _level, int _eval, int _alpha, int _beta) {
		level = (byte) _level;
		eval = _eval;	 
	}
	
	public void update(int _level, int _eval, int _alpha, int _beta) {
		
		if (_level > level) {
			
			init(_level, _eval, _alpha, _beta);
			
		} else if (_level == level) {		
			
			if (eval != _eval) {
				//throw new IllegalStateException("eval=" + eval + ", _eval=" + _eval);
				eval = _eval;
			}
			
		} else {
			//TODO
			//throw new IllegalStateException();
		}
	}
	

	public int getLevel() {
		return level;
	}


	@Override
	public int getEval() {
		return eval;
	}


	@Override
	public boolean isSketch() {
		//if (!isExact()) {
			throw new IllegalStateException();
		//}
		//return false;
	}


	@Override
	public int getLowerBound() {
		throw new IllegalStateException();
	}


	@Override
	public int getUpperBound() {
		throw new IllegalStateException();
	}
}
