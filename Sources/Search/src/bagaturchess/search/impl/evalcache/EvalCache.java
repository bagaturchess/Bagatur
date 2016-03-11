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


public class EvalCache extends LRUMapLongObject<IEvalEntry> implements IEvalCache {
	
	public EvalCache(int maxsize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new EvalEntryFactory(), maxsize, fillWithDummyEntries, _semaphore);
	}
	
	public IEvalEntry get(long key) {
		EvalEntry result =  (EvalEntry) super.getAndUpdateLRU(key);
		return result;
	}
	
	public void put(long hashkey, int eval, boolean sketch) {
		IEvalEntry entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			//Multithreaded access
		} else {
			entry = associateEntry(hashkey);
		}
		((EvalEntry)entry).setEval((int) eval);
		((EvalEntry)entry).setSketch(sketch);
	}

	@Override
	public void put(long hashkey, int level, int eval, int alpha, int beta) {
		throw new UnsupportedOperationException();
	}
}
