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


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;


public class EntryEntryFactory1 implements DataObjectFactory<IEvalEntry> {

	
	private static final int TEST_SIZE = 100000;
	
	
	private int max_level;
	
	
	public EntryEntryFactory1(int _max_level) {
		max_level = _max_level;
	}
	
	
	public IEvalEntry createObject() {
		return new EvalEntry1(max_level);
	}

	
	public static final long getEntrySize() {
		long size = 0;
		
		System.gc();
		
		long memBefore = getFreeMemory();
		
		EvalEntry1[] all = new EvalEntry1[TEST_SIZE];
		for (int i=0; i<all.length; i++) {
			all[i] = new EvalEntry1(1);
		}
		long memAfter = getFreeMemory();
		
		size = (memBefore - memAfter) / TEST_SIZE;
		
		return size;
	}

	private static long getFreeMemory() {
		
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long memBefore = Runtime.getRuntime().freeMemory();
		free = free + (max - total);
		
		return memBefore;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getEntrySize());
	}
}

