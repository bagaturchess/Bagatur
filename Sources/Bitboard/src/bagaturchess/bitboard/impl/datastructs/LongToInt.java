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
package bagaturchess.bitboard.impl.datastructs;

public class LongToInt {
	
	public static int DUMMY_VALUE = -1;
	
	private static int DEFAULT_SIZE = 1024;
	
	int size = DEFAULT_SIZE;
	boolean[] used;
	long[] keys;
	int[] values;
	
	public LongToInt() {
		used = new boolean[size];
		keys = new long[size];
		values = new int[size];
	}
	
	public void put(long key, int value) {
		//System.out.println("PUT: " + key + " -> " + value);
		if (value == DUMMY_VALUE) {
			throw new IllegalStateException("Value " + value + " is special value used by the structure");
		}
		
		int hash = hash(key);
		
		if (used[hash]) {
			long oldKey = keys[hash];
			if (oldKey != key) {
				//Colleasion
				for (int i=hash; i<size; i++) {
					if (!used[i]) {
						used[i] = true;
						keys[i] = key;
						values[i] = value;
						break;
					}
					if (i == size - 1) {
						throw new IllegalStateException("Hash " + hash + " used from key " + oldKey);	
					}
				}
			}
			values[hash] = value;
		} else {
			used[hash] = true;
			keys[hash] = key;
			values[hash] = value;
		}
	}
	
	public void remove(long key) {
		//System.out.println("REM: " + key);
		int hash = hash(key);
		
		if (keys[hash] == key) {
			used[hash] = false;
		} else {
			for (int i=hash; i<size; i++) {
				if (used[i] && keys[i] == key) {
					used[i] = false;
					break;
				}
				if (!used[i] || i == size - 1) {
					throw new IllegalStateException("Key " + key + " not found");	
				}
			}
		}
	}
	
	public int get(long key) {
		//System.out.println("GET: " + key);
		int hash = hash(key);
		if (used[hash]) {
			if (keys[hash] == key) {
				return values[hash];
			} else {
				for (int i=hash; i<size; i++) {
					if (used[i] && keys[i] == key) {
						return values[i];
					}
					if (!used[i] || i == size - 1) {
						return DUMMY_VALUE;
						//throw new IllegalStateException("Key " + key + " not found i=" + i + " hash=" + hash);	
					}
				}
				throw new IllegalStateException();
			}
		} else return DUMMY_VALUE;
	}
	
	private static final int hash(long key) {
		int mod = (int) (key % (DEFAULT_SIZE - 1));
		if (mod < 0) {
			mod = -mod;
		}
		return mod;
	}
}
