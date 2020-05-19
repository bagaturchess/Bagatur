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


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.utils.SearchUtils;


public class EvalCache extends LRUMapLongObject<IEvalEntry> implements IEvalCache {
	
	
	public EvalCache(int max_level, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new EvalEntryFactory(max_level), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public IEvalEntry get(long key) {	
		return super.getAndUpdateLRU(key);
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
		
		EvalEntry entry = (EvalEntry) super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_level, (int)_eval);
		} else {
			entry = (EvalEntry) associateEntry(hashkey);
			entry.init(_level, (int)_eval);
		}
	}
	
	
	private static class EvalEntryFactory implements DataObjectFactory<IEvalEntry> {
		
		
		private int max_level;
		
		
		public EvalEntryFactory(int _max_level) {
			max_level = _max_level;
		}
		
		
		public IEvalEntry createObject() {
			return new EvalEntry(max_level);
		}
	}
}
