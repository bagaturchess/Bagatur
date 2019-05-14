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
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.utils.SearchUtils;


public class TPTable extends LRUMapLongObject<TPTEntry> {
	
	
	private long count_unique_inserts = 0;
	
	
	public TPTable(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new TPTEntryFactory(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public long getCount_UniqueInserts() {
		return count_unique_inserts;
	}
	
	public void clearCount_UniqueInserts() {
		count_unique_inserts = 0;
	}
	
	/*public void obsoleteAll() {
		long[] keys = map.getAllKeys();
		for (int i=0; i<keys.length; i++) {
			long key = keys[i];
			map.get(key).getValue().obsolete = true;
		}
	}*/
	
	public void correctAllDepths(final int reduction) {
		
		IValuesVisitor_HashMapLongObject<TPTEntry> visitor = new IValuesVisitor_HashMapLongObject<TPTEntry>() {
			@Override
			public void visit(TPTEntry entry) {
				entry.depth = (byte) Math.max(1, entry.depth - reduction);
			}
		};
		visitValues(visitor);
		
		/*long[] keys = map.getAllKeys();
		for (int i=0; i<keys.length; i++) {
			long key = keys[i];
			TPTEntry entry = map.get(key).getValue();
			entry.depth = (byte) Math.max(1, entry.depth - reduction);
		}*/
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
		
		//int _depth = _smaxdepth - _sdepth;
		
		/*if (_depth < skipDepth
				&& (_eval <= _alpha || _eval >= _beta) ) {
			return null;
		}*/
		
		TPTEntry entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			entry.update(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, movenumber);
		} else {
			entry = associateEntry(hashkey);
			entry.init(_smaxdepth, _sdepth, _colour, _eval, _alpha, _beta, _bestmove, movenumber);
			count_unique_inserts++;
		}
		
		return entry;
	}
	
	public TPTEntry getEntryByAtleastDepth(int depth, long hashkey) {
		
		TPTEntry entry = get(hashkey);
		
		if (entry != null) {
			if (entry.depth >= depth) {
				return entry;
			}
		}
		
		return null;
	}
	
	/*public TPTEntry getEntryByType(TPTEntryType type, long hashkey) {
	
	if (true) throw new IllegalStateException(); 
	
	lock.readLock().lock();
	TPTEntry entry = get(hashkey);
	lock.readLock().unlock();
	
	if (entry != null) {
		throw new IllegalStateException();
	}
	return null;
}*/
	
	/*public TPTEntry getEntryByExactDepth(int depth, long hashkey) {
	
	if (true) throw new IllegalStateException(); 
	
	lock.readLock().lock();
	TPTEntry entry = get(hashkey);
	lock.readLock().unlock();
	
	if (entry != null) {
		if (entry.depth == depth) {
			return entry;
		}
	}
	return null;
}*/
	
	/*public void makeAllDirty() {
		
		if (true) throw new IllegalStateException(); 
		
		lock.writeLock().lock();
		long[] keys = map.getAllKeys();
		for (int i=0; i<keys.length; i++) {
			long key = keys[i];
			get(key).setDirty(true);
		}
		lock.writeLock().unlock();
	}*/
}
