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


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.IValuesVisitor_HashMapLongObject;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.alg.SearchUtils;


public class TTable_Impl1 extends LRUMapLongObject<TTable_Impl1.TPTEntry> implements ITTable {
	
	
	public TTable_Impl1(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new TPTEntryFactory(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.tpt.ITTable#correctAllDepths(int)
	 */
	@Override
	public void correctAllDepths(final int reduction) {
		
		IValuesVisitor_HashMapLongObject<TPTEntry> visitor = new IValuesVisitor_HashMapLongObject<TPTEntry>() {
			@Override
			public void visit(TPTEntry entry) {
				entry.depth = (byte) Math.max(1, entry.depth - reduction);
			}
		};
		visitValues(visitor);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.tpt.ITTable#get(long, bagaturchess.search.impl.tpt.ITTEntry)
	 */
	@Override
	public void get(long key, ITTEntry entry) {
		
		entry.setIsEmpty(true);
		
		TPTEntry mem = super.getAndUpdateLRU(key);
		
		if (mem != null) {
			
			entry.setIsEmpty(false);
			entry.setDepth(mem.getDepth());
			
			if (mem.isExact()) {
				entry.setFlag(ITTEntry.FLAG_EXACT);
				entry.setEval(mem.getLowerBound());
				entry.setBestMove(mem.getBestMove_lower());
			} else if (mem.getBestMove_lower() != 0) {
				entry.setFlag(ITTEntry.FLAG_LOWER);
				entry.setEval(mem.getLowerBound());
				entry.setBestMove(mem.getBestMove_lower());
			} else {
				entry.setFlag(ITTEntry.FLAG_UPPER);
				entry.setEval(mem.getUpperBound());
				entry.setBestMove(mem.getBestMove_upper());
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.tpt.ITTable#put(long, int, int, int, int, int, int, int, byte)
	 */
	@Override
	public void put(long hashkey, int _depth, int _eval, int _alpha, int _beta, int _bestmove) {
		
		if (_bestmove == 0) {
			throw new IllegalStateException();
		}
		
		if (_eval == ISearch.MAX || _eval == ISearch.MIN) {
			throw new IllegalStateException("_eval=" + _eval);
		}

		if (_eval >= ISearch.MAX_MAT_INTERVAL || _eval <= -ISearch.MAX_MAT_INTERVAL) {
			if (!SearchUtils.isMateVal(_eval)) {
				throw new IllegalStateException("not mate val _eval=" + _eval);
			}
		}
		
		if (SearchUtils.isMateVal(_eval)) {
			return;
		}
		
		TPTEntry entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_depth, _eval, _alpha, _beta, _bestmove);
		} else {
			entry = associateEntry(hashkey);
			entry.init(_depth, _eval, _alpha, _beta, _bestmove);
		}
	}
	
	
	private static class TPTEntryFactory implements DataObjectFactory<TPTEntry> {
		public TPTEntry createObject() {
			return new TPTEntry();
		}
	}
	
	
	protected static final class TPTEntry {
		
		
		byte depth;
		int lower;
		int upper;
		int bestmove_lower;
		int bestmove_upper;
		
		
		public TPTEntry() {
			
		}
		
		
		public TPTEntry(int _depth, int _eval, int _alpha, int _beta, int _bestmove) {
			
			init(_depth, _eval, _alpha, _beta, _bestmove);
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
		
		
		public void init(int _depth, int _eval, int _alpha, int _beta, int _bestmove) {
			
			depth = (byte) _depth;
			
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
		
		public void update(int _depth, int _eval, int _alpha, int _beta, int _bestmove) {
			
			/*if (true) {
				init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, _movenumber);
				return;
			}*/
			
			if (_depth > depth) {
				
				init(_depth, _eval, _alpha, _beta, _bestmove);
				
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
}
