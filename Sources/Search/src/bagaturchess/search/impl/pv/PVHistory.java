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
package bagaturchess.search.impl.pv;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class PVHistory {

	private ReadWriteLock lock;
	private Map<Long, PVHistoryEntry> pvs;
	
	public PVHistory() {
		lock = new ReentrantReadWriteLock();
		pvs = new HashMap<Long, PVHistoryEntry>();
	}
	
	public PVHistoryEntry getPV(long hashkey) {
		lock.readLock().lock();
		PVHistoryEntry result = pvs.get(hashkey);
		lock.readLock().unlock();
		return result;
	}
	
	public void putPV(long hashkey, PVHistoryEntry entry) {
		lock.writeLock().lock();
		PVHistoryEntry old = pvs.get(hashkey);
		if (old != null) {
			if (entry.getDepth() > old.getDepth()) {
				pvs.put(hashkey, entry);
			} else if (entry.getDepth() == old.getDepth()) {
				if (entry.getEval() > old.getEval()) {
					pvs.put(hashkey, entry);
				} else if (entry.getEval() == old.getEval()) {
					if (entry.getPv().length >= old.getPv().length) {
						pvs.put(hashkey, entry);
					}
				}
			}
		} else {
			pvs.put(hashkey, entry);
		}
		lock.writeLock().unlock();
	}

	public void clear() {
		lock.writeLock().lock();
		pvs.clear();
		lock.writeLock().unlock();
	}
}
