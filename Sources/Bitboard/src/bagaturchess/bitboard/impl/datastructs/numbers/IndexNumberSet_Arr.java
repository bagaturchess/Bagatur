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
package bagaturchess.bitboard.impl.datastructs.numbers;


import javax.print.attribute.standard.MediaSize.Engineering;

import bagaturchess.bitboard.common.Properties;


/**
 * Created by Krasimir Topchiyski
 * Date: 2004-3-13
 * Time: 17:28:45
 *
 */
public class IndexNumberSet_Arr implements NumberSet {
	private int[] data;
	private int size;
	
	public IndexNumberSet_Arr(int max) {
		data = new int[max];
		size = 0;
	}

    public int getIndex(int aNumber) {
    	throw new IllegalStateException();
    }

    public boolean contains(int aNumber) {
		for (int i=0; i<size; i++) {
			if (data[i] == aNumber) return true; 
		}
		return false;
	}

	public void add(int aNumber) {
		data[size++] = aNumber;
	}

	public int remove(int aNumber) {
		boolean found = false;
		for (int i=0; i<size; i++) {
			if (data[i] == aNumber) {
				data[i] = data[size - 1];
				size--;
				found = true;
				break;
			}
		}
		
		return 0;
	}

	public int getFirst() {
		
		throw new IllegalStateException();
		
	}

	public int getLast() {
		throw new IllegalStateException();
	}
	
	public void clear() {
		size = 0;
		//mData[0][0] = 0;
	}

	public int getDataSize() {
		//return mData[0][0];
		return size;
	}

	public int[] getData() {
		return data;
	}

	public String toString() {
		String result = "[";
		for (int i = 0; i < size; i++) {
		//for (int i = 0; i < mData[0][0]; i++) {
			result += data[i] + " ";
		}
		result += "]";
		return result;
	}

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof IndexNumberSet_Arr) {
			IndexNumberSet_Arr numberSet = (IndexNumberSet_Arr) obj;
			result = containsAll(numberSet) && numberSet.containsAll(this);
		}
		return result;
	}

	public boolean containsAll(IndexNumberSet_Arr numberSet) {
		boolean result = true;
		int data[] = numberSet.getData();
		int count = numberSet.getDataSize();
		for (int i = 0; i < count; i++) {
			int number = data[i];
			if (!contains(number)) {
				//System.out.println( "INS ID not found: " + number );
				result = false;
				break;
			}
		}
		return result;
	}

	public IndexNumberSet_Arr clone() {
		IndexNumberSet_Arr clone = new IndexNumberSet_Arr(data.length);
		for (int i = 0; i < size; i++) {
		//for (int i = 0; i < mData[0][0]; i++) {
			clone.add(data[i]);
		}
		return clone;
	}
	
	public void finishIteration() {
	}

	public boolean inIteration() {
		return true;
	}
}
