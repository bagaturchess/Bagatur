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
package bagaturchess.opening.impl.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import bagaturchess.opening.api.IOpeningEntry;

public class Entry_BaseImpl implements IOpeningEntry, Serializable {
	
	
	private static final long serialVersionUID = -527726372381591843L;
	
	
	private long hashkey;
	
	private int hits;
	
	private int[] evals;
	private int[] moves;

	private int[] counts;
	
	private int[] probsWin;
	private int[] probsDraw;
	private int[] probsLose;
	
	
	public Entry_BaseImpl(long _hashkey) {
		hashkey = _hashkey;
	}
	
	
	public void add(int move, int result) {
		
		if (hits > 10000000) {
			throw new IllegalStateException();
		}

		
		hits++;
		
		if (moves == null) {
			
			moves = new int[1];
			counts = new int[1];
			probsLose = new int[1];
			probsDraw = new int[1];
			probsWin = new int[1];
			
			moves[0] = move;
			counts[0] = 1;
			
			switch(result) {
				case -1:
					probsLose[0] = 1;
					break;
				case 0:
					probsDraw[0] = 1;
					break;
				case 1:
					probsWin[0] = 1;
					break;
				default:
					throw new IllegalStateException();
			}
			
		} else {
			
			boolean found = false;
			for (int i=0; i<moves.length; i++) {
				int cur = moves[i];
				if (cur == move) {
					counts[i]++;
					
					switch(result) {
						case -1:
							probsLose[i]++;
							break;
						case 0:
							probsDraw[i]++;
							break;
						case 1:
							probsWin[i]++;
							break;
						default:
							throw new IllegalStateException();
					}
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				moves = add_checked(moves, move);
				counts = add(counts, 1);
				
				switch(result) {
					case -1:
						probsLose = add(probsLose, 1);
						probsDraw = add(probsDraw, 0);
						probsWin = add(probsWin, 0);
						break;
					case 0:
						probsLose = add(probsLose, 0);
						probsDraw = add(probsDraw, 1);
						probsWin = add(probsWin, 0);
						break;
					case 1:
						probsLose = add(probsLose, 0);
						probsDraw = add(probsDraw, 0);
						probsWin = add(probsWin, 1);
						break;
					default:
						throw new IllegalStateException();
				}
			}
		}
	}
	
	private static final int[] add_checked(int[] arr, int el) {
		int[] result = new int[arr.length + 1]; 
		
		for (int i=0; i<arr.length; i++) {
			if (el == arr[i]) {
				throw new IllegalStateException();
			}
			result[i] = arr[i];
		}
		result[arr.length] = el;
		
		return result;
	}
	
	static final int[] add(int[] arr, int el) {
		int[] result = new int[arr.length + 1]; 
		
		for (int i=0; i<arr.length; i++) {
			result[i] = arr[i];
		}
		result[arr.length] = el;
		
		return result;
	}
	
	
	public int getRandomEntry(int power) {
		
		int all_probs = 0;
		for (int i=0; i<counts.length; i++) {
			all_probs += Math.pow(counts[i] , power);
		}
		
		int prob_index = (int) Math.round((all_probs - 1) * Math.random());
		
		int index = 0;
		int cur_probs = 0;
		for (int i=0; i<counts.length; i++) {
			cur_probs += Math.pow(counts[i] , power);
			if (cur_probs > prob_index) {
				index = i;
				break;
			}
		}
		
		return moves[index];
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
    //stream.defaultWriteObject();
    
    stream.writeLong(hashkey);
    stream.writeInt(hits);
    stream.writeByte(moves.length);
    for (int i=0; i<moves.length; i++) {
    	stream.writeInt(moves[i]);
    	stream.writeInt(counts[i]);
    	//stream.writeInt(probsWin[i]);
    	//stream.writeInt(probsDraw[i]);
    	//stream.writeInt(probsLose[i]);
    }
  }

  private void readObject(ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    //stream.defaultReadObject();
    
    hashkey = stream.readLong();
    hits = stream.readInt();
    int moves_count = stream.readByte();
    
    moves = new int[moves_count];
    counts = new int[moves_count];
    //probsWin = new int[moves_count];
    //probsDraw = new int[moves_count];
    //probsLose = new int[moves_count];
    
    for (int i=0; i<moves_count; i++) {
    	moves[i] = stream.readInt();
    	counts[i] = stream.readInt();
    	//probsWin[i] = stream.readInt();
    	//probsDraw[i] = stream.readInt();
    	//probsLose[i] = stream.readInt();
    }
  }

	public long getHashkey() {
		return hashkey;
	}

	public int getWeight() {
		return hits;
	}

	public int[] getMoves() {
		return moves;
	}
	
	public int[] getCounts() {
		return counts;
	}
}
