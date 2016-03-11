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
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.utils.SearchUtils;


public class EvalCache1 extends LRUMapLongObject<IEvalEntry> implements IEvalCache {
	
	
	public EvalCache1(int max_level, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new EntryEntryFactory1(max_level), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public IEvalEntry get(long key) {	
		return super.getAndUpdateLRU(key);
	}
	
	
	public void put(long hashkey, int _level, int _eval, int _alpha, int _beta) {
		
		if (_eval == ISearch.MAX || _eval == ISearch.MIN) {
			throw new IllegalStateException("_eval=" + _eval);
		}

		if (_eval >= ISearch.MAX_MAT_INTERVAL || _eval <= -ISearch.MAX_MAT_INTERVAL) {
			if (!SearchUtils.isMateVal(_eval)) {
				throw new IllegalStateException("not mate val _eval=" + _eval);
			}
		}
		
		EvalEntry1 entry = (EvalEntry1) super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_level, _eval, _alpha, _beta);
		} else {
			entry = (EvalEntry1) associateEntry(hashkey);
			entry.init(_level, _eval, _alpha, _beta);
		}
	}


	@Override
	public void put(long hashkey, int eval, boolean sketch) {
		put(hashkey, 1, eval, ISearch.MIN, ISearch.MAX);
	}
}
