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

import bagaturchess.bitboard.common.Properties;


/**
 * Created by Krasimir Topchiyski
 * Date: 2004-3-13
 * Time: 17:28:45
 *
 */
public class IndexNumberMap {
	private int[][] mData;
	private int size;
	private long[] mValues;

	public IndexNumberMap(int aMaxNumber) {
		mData = new int[2][aMaxNumber];
		mValues = new long[aMaxNumber];
		size = 0;
	}

  private int getIndex(int aNumber) {
        int index = mData[0][aNumber];
        return (mData[1][index] == aNumber && size > index) ? index : -1;
  }

  public boolean contains(int aNumber) {
		return getIndex(aNumber) != -1;
	}

  public long getValue(int aNumber) {
    int index = mData[0][aNumber];
    if (mData[1][index] == aNumber && size > index) {
    	return mValues[aNumber];
    } else {
    	throw new IllegalStateException();
    }
	}
  
	public void add(int aNumber, long value) {
		if (contains(aNumber)) {
			throw new IllegalStateException("Number " + aNumber + " already exists!");
		}
		
		if (value == 0) {
			throw new IllegalStateException();
		}
		//int size = mData[0][0]++;
		//number is never 0, so I use cell arr[ 0 ][ 0 ] to store current size of arr[ 1 ].
		mData[0][aNumber] = size;
		mData[1][size] = aNumber;
		mValues[aNumber] = value;
		size++;
	}

	public int remove(int aNumber) {
		//<debug>
		if (Properties.DEBUG_MODE) {
			int index = mData[0][aNumber];
			//if (mData[1][index] != aNumber && mData[0][0] > index) {
			if (mData[1][index] != aNumber && size > index) {
				throw new IllegalStateException("Number " + aNumber + " not found!");
			}
		}
		//</debug>
		if (contains(aNumber)) {
			int index = mData[0][aNumber];
			size--;
			//int size = --mData[0][0];
			int lastNumber = mData[1][size];
			mData[1][index] = lastNumber;
			mData[0][lastNumber] = index;
		}
        return 0;
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
		return mData[1];
	}
}
