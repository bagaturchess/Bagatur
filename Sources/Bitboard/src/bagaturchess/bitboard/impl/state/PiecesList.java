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
package bagaturchess.bitboard.impl.state;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.Fields;


public class PiecesList {
	
	
	private int[] data;
	private int size;
	
	private IBoard board;
	
	
	public PiecesList(IBoard _board, int max) {
		board = _board;
		data = new int[max];
		size = 0;
	}


   public boolean contains(int aNumber) {
		for (int i=0; i<size; i++) {
			if (data[i] == aNumber) return true; 
		}
		return false;
	}

  public void set(int from, int to) {
  	boolean ok = false;
		for (int i=0; i<size; i++) {
			if (data[i] == from) {
				data[i] = to;
				ok = true;
			}
		}
		
		if (!ok) {
			throw new IllegalStateException(board + " FROM_TO=" + Fields.getFieldSign(from) + "-" + Fields.getFieldSign(to));
		}
  }
   
	public void add(int aNumber) {
		if (size == data.length) {
			throw new IllegalStateException(board + " ADDING " + aNumber);
		}
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
		
		if (!found) {
			throw new IllegalStateException(board + " REMOVING " + aNumber);
		}
		
		return 0;
	}

	/*public int getFirst() {
		
		throw new IllegalStateException();
		
	}

	public int getLast() {
		throw new IllegalStateException();
	}*/
	
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
}
