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
import bagaturchess.search.impl.utils.SearchUtils;


public class TPTable extends LRUMapLongObject<TPTEntry> {
	
	
	public TPTable(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new TPTEntryFactory(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public void correctAllDepths(final int reduction) {
		
		IValuesVisitor_HashMapLongObject<TPTEntry> visitor = new IValuesVisitor_HashMapLongObject<TPTEntry>() {
			@Override
			public void visit(TPTEntry entry) {
				entry.depth = (byte) Math.max(1, entry.depth - reduction);
			}
		};
		visitValues(visitor);
	}
	
	
	public TPTEntry get(long key) {
		
		return super.getAndUpdateLRU(key);
	}
	
	
	public TPTEntry put(long hashkey,
			int _smaxdepth, int _sdepth, 
			int _colour,
			int _eval, int _alpha, int _beta, int _bestmove,
			byte movenumber) {
		
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
			return null;
		}
		
		TPTEntry entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, movenumber);
		} else {
			entry = associateEntry(hashkey);
			entry.init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, movenumber);
		}
		
		return entry;
	}
	
	
	private static class TPTEntryFactory implements DataObjectFactory<TPTEntry> {
		public TPTEntry createObject() {
			return new TPTEntry();
		}
	}
}
