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


import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearch;



public class TPTEntry {
	
	
	public static final int MIN_VALUE = ISearch.MIN;
	public static final int MAX_VALUE = ISearch.MAX;
	
	public static final int NO_MOVE = 0;
	
	
	//byte sdepth;
	byte depth;
	//byte colour;
	
	int lower;
	int upper;
	int bestmove_lower;
	int bestmove_upper;
	
	//byte movenumber;
	//boolean exact = false;
	
	//boolean obsolete = false;
	
	public TPTEntry() {
		
	}
	
	public TPTEntry(int _smaxdepth, int _sdepth, int _colour,
			int _eval, int _alpha, int _beta, int _bestmove) {
		
		init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, (byte)0);
	}

	public String toString() {
		String result = "";
		
		//result += "sdepth=" + sdepth;
		result += " depth=" + depth;
		//result += ", colour=" + colour;
		result += ", lower=" + lower;
		result += ", upper=" + upper;
		result += ", bestmove_lower=" + MoveInt.moveToString(bestmove_lower);
		result += ", bestmove_upper=" + MoveInt.moveToString(bestmove_upper);
		
		return result;
	}
	
	public void init(int _smaxdepth, int _sdepth, int _colour, 
			int _eval, int _alpha, int _beta, int _bestmove, byte _movenumber) {
		
		//obsolete = false;
		
		//sdepth = (byte)_sdepth;
		depth = (byte) (_smaxdepth - _sdepth);
		//colour = (byte) _colour;
		//movenumber = _movenumber;
		
		if (_eval > _alpha && _eval < _beta) {
			//exact = true;
			lower = _eval;
			upper = _eval;
			bestmove_lower = _bestmove;
			bestmove_upper = _bestmove;
		} else {
			//exact = false;
			if (_eval >= _beta) { //_eval is lower bound
				lower = _eval;
				bestmove_lower = _bestmove;
				bestmove_upper = NO_MOVE;
				upper = TPTEntry.MAX_VALUE;
			} else if (_eval <= _alpha) { //_eval is upper bound
				lower = TPTEntry.MIN_VALUE;
				bestmove_lower = NO_MOVE;
				bestmove_upper = _bestmove;
				upper = _eval;
			} else {
				throw new IllegalStateException();
			}
		}		 
	}
	
	public void update(int _smaxdepth, int _sdepth, int _colour,
			int _eval, int _alpha, int _beta, int _bestmove, byte _movenumber) {
		
		//if (_colour != colour) {
		//	throw new IllegalStateException("Different colour. Existing entry with " + colour + " but updated with " + _colour);
		//}
		
		//movenumber = _movenumber;
		
		byte _depth = (byte) (_smaxdepth - _sdepth);
		
		/*if (true) {
			init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
			return;
		}*/
		
		if (_depth > depth) {
			
			init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
			
		} else if (_depth == depth) {		
			
			//if (isExact() /*&& (_eval > _alpha && _eval < _beta)*/) {
			//	//throw new IllegalStateException();
			//	return;
			//}
			
			if (isExact()) {
				//System.out.println("PINKO");
				//return;
			}
			
			if (_eval > _alpha && _eval < _beta) {
				
				//obsolete = false;
				
				//exact = true;
				lower = _eval;
				upper = _eval;
				bestmove_lower = _bestmove;
				bestmove_upper = _bestmove;
				
			} else {
				
				if (_eval >= _beta) { // _eval is lower bound
					if (_eval >/*=*/ lower) {
						
						//obsolete = false;
						
						lower = _eval;
						bestmove_lower = _bestmove;
					}
				} else if (_eval <= _alpha) { // _eval is upper bound
					if (_eval </*=*/ upper) {
						
						//obsolete = false;
						
						upper = _eval;
						bestmove_upper = _bestmove;
					}
				} else {
					throw new IllegalStateException();
				}
			}
		} else { //_depth < depth
			//if (_eval > _alpha && _eval < _beta) {
				//SHIT
			//	init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
			//}
		}
		
		if (lower == upper && (lower == MIN_VALUE || lower == MAX_VALUE)) {
			throw new IllegalStateException();
		}
	}
	
	//public int getColour() {
	//	return colour;
	//}
	
	public boolean isExact() {
		return isExact(0);
	}
	
	public boolean isExact(int trustWindow) {
		//return false;
		return lower >= upper;
		//return Math.abs(lower - upper) <= trustWindow;
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
		//if (isExact()) {
		//	return bestmove_lower;
		//}
		return bestmove_upper;
	}

	public int getDepth() {
		return depth;
	}

	/*public boolean isDirty() {
		throw new IllegalStateException();
	}

	public void setDirty(boolean dirty) {
		throw new IllegalStateException();
	}*/

	//public boolean isObsolete() {
	//	return obsolete;
	//}
}
