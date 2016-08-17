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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bagaturchess.bitboard.impl.datastructs.HashMapLongObject;
import bagaturchess.opening.api.OpeningBook;


public class OpeningBookImpl_FullEntries implements OpeningBook {
	
	
	private static final long serialVersionUID = 7305402510512589014L;
	
	
	public HashMapLongObject<Entry_BaseImpl> entries;
	
	
	public OpeningBookImpl_FullEntries() {
	}
	
	public int get(long hashkey, int colour) {
		
		if (entries == null) {
			entries = new HashMapLongObject<Entry_BaseImpl>();
		}
		
		Entry_BaseImpl moves = entries.get(hashkey);
		if (moves != null) {
			return moves.getRandomEntry(0);
		}
		
		return 0;
	}
	
	public Entry_BaseImpl getEntry(long hashkey, int colour) {
		
		if (entries == null) {
			entries = new HashMapLongObject<Entry_BaseImpl>();
		}
		
		return entries.get(hashkey);
	}
	
	public void add(long hashkey, int move) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(long hashkey, int move, int result) {

		
		if (entries == null) {
			entries = new HashMapLongObject<Entry_BaseImpl>();
		}
		
		Entry_BaseImpl existing = entries.get(hashkey);
		if (existing == null) {
			existing = new Entry_BaseImpl(hashkey);
			entries.put(hashkey, existing);
		}
		
		existing.add(move, result);
	}
	
	int[][] result;
	
	public int[][] getAllMovesAndCounts(long hashkey, int colour) {
		Entry_BaseImpl moves = entries.get(hashkey);
		
		if (moves != null) {
			if (result == null) {
				result = new int[2][];
			}
			result[0] = moves.getMoves();
			result[1] = moves.getCounts();
			return result;
		}
		
		return null;
	}
	
	public void store(String outFileName) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFileName));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		return entries.size();
	}

	public void unload() {
		entries.clear();
	}
	
	 private void writeObject(ObjectOutputStream stream) throws IOException {
	    //stream.defaultWriteObject();
		 stream.writeInt(entries.size());
		 Object[] vals = entries.getAllValues();
		 for (Object cur: vals) {
			 stream.writeObject(cur);
		 }
	  }
	
	  private void readObject(ObjectInputStream stream) throws IOException,
	      ClassNotFoundException {
	    //stream.defaultReadObject();
	    
			 int size = stream.readInt();
			 entries = new HashMapLongObject<Entry_BaseImpl>();
			 for (int i=0; i<size; i++) {
				 Entry_BaseImpl cur = (Entry_BaseImpl) stream.readObject();
				 entries.put(cur.getHashkey(), cur);
			 }
	  }
}
