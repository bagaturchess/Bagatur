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
package bagaturchess.search.impl.eval.cache;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.alg.SearchUtils;


public class EvalCache_Impl1 extends LRUMapLongObject<IEvalEntry> implements IEvalCache {
	
	
	public EvalCache_Impl1(int max_level, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new EvalEntryFactory(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public void get(long key, IEvalEntry entry) {
		
		EvalEntry_BaseImpl mem = (EvalEntry_BaseImpl) super.getAndUpdateLRU(key);
		
		entry.setIsEmpty(true);
		
		if (mem!= null) {
			entry.setIsEmpty(false);
			entry.setEval(mem.getEval());
			entry.setLevel(mem.getLevel());
		}
	}
	
	
	public void put(long hashkey, int _level, double _eval) {
		
		if (_eval == ISearch.MAX || _eval == ISearch.MIN) {
			throw new IllegalStateException("_eval=" + _eval);
		}

		if (_eval >= ISearch.MAX_MAT_INTERVAL || _eval <= -ISearch.MAX_MAT_INTERVAL) {
			if (!SearchUtils.isMateVal((int)_eval)) {
				throw new IllegalStateException("not mate val _eval=" + _eval);
			}
		}
		
		EvalEntryImpl entry = (EvalEntryImpl) super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_level, (int)_eval);
		} else {
			entry = (EvalEntryImpl) associateEntry(hashkey);
			entry.init(_level, (int)_eval);
		}
	}
	
	
	private static class EvalEntryFactory implements DataObjectFactory<IEvalEntry> {
		
		
		public EvalEntryFactory() {
		}
		
		
		public IEvalEntry createObject() {
			return new EvalEntryImpl();
		}
	}
	
	
	private static class EvalEntryImpl extends EvalEntry_BaseImpl {
		
		
		private void init(int _level, int _eval) {
			level = (byte) _level;
			eval = _eval;	 
		}
		
		private void update(int _level, int _eval) {
			
			if (_level > level) {
				
				init(_level, _eval);
				
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
		
		
		@Override
		public String toString() {
			String result = "";
			
			result += " level=" + level;
			result += ", eval=" + eval;
			
			return result;
		}
	}
}
