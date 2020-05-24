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
package bagaturchess.search.impl.tpt;


import bagaturchess.search.api.internal.ISearch;


public class TPTEntry {
	
	
	public static final int FLAG_EXACT = 0;
	public static final int FLAG_UPPER = 1;
	public static final int FLAG_LOWER = 2;
	
	
	byte depth;
	int lower;
	int upper;
	int bestmove_lower;
	int bestmove_upper;
	
	
	public TPTEntry() {
		
	}
	
	
	public TPTEntry(int _smaxdepth, int _sdepth, int _colour,
			int _eval, int _alpha, int _beta, int _bestmove) {
		
		init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, (byte)0);
	}
	
	
	public String toString() {
		String result = "";
		
		result += " depth=" + depth;
		result += ", lower=" + lower;
		result += ", upper=" + upper;
		//result += ", bestmove_lower=" + MoveInt.moveToString(bestmove_lower);
		//result += ", bestmove_upper=" + MoveInt.moveToString(bestmove_upper);
		
		return result;
	}
	
	
	public void init(int _smaxdepth, int _sdepth, int _colour, 
			int _eval, int _alpha, int _beta, int _bestmove, byte _movenumber) {
		
		depth = (byte) (_smaxdepth - _sdepth);
		
		if (_eval > _alpha && _eval < _beta) {
			lower = _eval;
			upper = _eval;
			bestmove_lower = _bestmove;
			bestmove_upper = _bestmove;
		} else {
			if (_eval >= _beta) { //_eval is lower bound
				lower = _eval;
				bestmove_lower = _bestmove;
				bestmove_upper = 0;
				upper = ISearch.MAX;
			} else if (_eval <= _alpha) { //_eval is upper bound
				lower = ISearch.MIN;
				bestmove_lower = 0;
				bestmove_upper = _bestmove;
				upper = _eval;
			} else {
				throw new IllegalStateException();
			}
		}		 
	}
	
	public void update(int _smaxdepth, int _sdepth, int _colour,
			int _eval, int _alpha, int _beta, int _bestmove, byte _movenumber) {
		
		byte _depth = (byte) (_smaxdepth - _sdepth);
		
		/*if (true) {
			init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
			return;
		}*/
		
		if (_depth > depth) {
			
			init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
			
		} else if (_depth == depth) {		
			
			if (_eval > _alpha && _eval < _beta) {
				
				lower = _eval;
				upper = _eval;
				bestmove_lower = _bestmove;
				bestmove_upper = _bestmove;
				
			} else {
				
				if (_eval >= _beta) { // _eval is lower bound
					if (_eval >/*=*/ lower) {
						
						lower = _eval;
						bestmove_lower = _bestmove;
					}
				} else if (_eval <= _alpha) { // _eval is upper bound
					if (_eval </*=*/ upper) {
						
						upper = _eval;
						bestmove_upper = _bestmove;
					}
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		if (lower == upper && (lower == ISearch.MIN || lower == ISearch.MAX)) {
			throw new IllegalStateException();
		}
	}
	
	
	public boolean isExact() {
		return lower >= upper;
	}
	
	
	public int getLowerBound() {
		return lower;
	}
	
	
	public int getUpperBound() {
		return upper;
	}
	
	
	public int getBestMove_lower() {
		return bestmove_lower;
	}
	
	
	public int getBestMove_upper() {
		return bestmove_upper;
	}
	
	
	public int getDepth() {
		return depth;
	}
}
