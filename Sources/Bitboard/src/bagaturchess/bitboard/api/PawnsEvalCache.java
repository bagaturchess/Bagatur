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
package bagaturchess.bitboard.api;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public class PawnsEvalCache extends LRUMapLongObject<PawnsModelEval> {
	
	
	public PawnsEvalCache(DataObjectFactory<PawnsModelEval> _factory, int _maxSize, boolean fillWithDummyEntries,
			IBinarySemaphore _semaphore) {
		super(_factory, _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	public PawnsModelEval get(long key) {
		PawnsModelEval result =  super.getAndUpdateLRU(key);
		return result;
	}
	
	public PawnsModelEval put(long hashkey) {
		PawnsModelEval entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			throw new IllegalStateException();
			//return entry; //Multithreaded access
			//throw new IllegalStateException();
		} else {
			entry = associateEntry(hashkey);
		}
		
		return entry;
	}
}
