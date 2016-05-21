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
import java.util.HashMap;
import java.util.Map;

import bagaturchess.opening.api.OpeningBook;

public class OpeningBookImpl_OnlyHashkeys implements OpeningBook {

	private static final long serialVersionUID = 7305402510512589014L;
	
	public Map<Long, Integer> keys;
	
	public OpeningBookImpl_OnlyHashkeys() {
		keys = new HashMap<Long, Integer>();
	}
	  
	public int get(long hashkey, int colour) {
		
		throw new UnsupportedOperationException();
	}
	
	public void remove(long hashkey) {
		if (!keys.containsKey(hashkey)) {
			throw new IllegalStateException();
		}
		keys.remove(hashkey);
	}
	
	public void add(long hashkey, int move) {
		Integer existing = keys.get(hashkey);
		if (existing != null) {
			keys.put(hashkey, existing + 1);
		} else {
			keys.put(hashkey, 1);
		}
	}
	
	@Override
	public void add(long hashkey, int move, int result) {
		throw new UnsupportedOperationException();
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
		return keys.size();
	}

	public void unload() {
		keys.clear();
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		//stream.defaultWriteObject();
	 stream.writeInt(keys.size());
	 for (Long key: keys.keySet()) {
		 stream.writeLong(key);
		 stream.writeInt(keys.get(key));
	 }
  }
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    //stream.defaultReadObject();
    
		 int size = stream.readInt();
		 keys = new HashMap<Long, Integer>();
		 
		 for (int i=0; i<size; i++) {
			 long key = stream.readLong();
			 int val = stream.readInt();
			 keys.put(key, val);
		 }
  }

	public Entry_BaseImpl getEntry(long hashkey, int colour) {
		throw new UnsupportedOperationException();
	}

	public int[][] getAllMovesAndCounts(long hashkey, int colour) {
		throw new UnsupportedOperationException();
	}
}
